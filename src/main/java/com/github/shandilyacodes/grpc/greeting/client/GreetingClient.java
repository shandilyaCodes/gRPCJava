package com.github.shandilyacodes.grpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello, I am a gRPC Client!");
        GreetingClient client = new GreetingClient();
        client.run();
    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        // doUnaryCall(channel);
        // doServerStreamingCall(channel);
        doClientStreamingCall(channel);
        System.out.println("Shutting Down the Channel...");
        channel.shutdown();
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // We get a response from the server
                System.out.println("Received a response from the Server");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                // We get an error from the server
            }

            @Override
            public void onCompleted() {
                // The server is done sending us the data
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        // Streaming message # 1
        System.out.println("Sending message # 1");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Ramendu").build())
                .build());

        // Streaming message # 2
        System.out.println("Sending message # 2");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Shandilya").build())
                .build());

        // Streaming message # 3
        System.out.println("Sending message # 3");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Unarmed").build())
                .build());

        // We tell the server that Client is done sending the data
        requestObserver.onCompleted();

        try {
            latch.await(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doUnaryCall(ManagedChannel channel) {
        // Created a new synchronous blocking stub
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Create the ProtoBuff Greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Ramendu")
                .setLastName("Shandilya")
                .build();

        // Create ht ProtoBuff Greeting Request
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        // Call the greet method using the greet client
        GreetResponse greetResponse = greetClient.greet(greetRequest);

        // Do something with the result / response
        System.out.println(greetResponse.getResult());
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        // Create the sync greet client using a blocking stub
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // We prepare the GreetManyTimesRequest ProtoBuff
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Ramendu")).build();

        // We stream the responses in a blocking fashion
        greetClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(res -> System.out.println(res.getResult()));
    }
}