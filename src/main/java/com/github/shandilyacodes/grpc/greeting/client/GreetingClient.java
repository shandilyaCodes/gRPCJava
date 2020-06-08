package com.github.shandilyacodes.grpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

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
        doUnaryCall(channel);
        doServerStreamingCall(channel);

        System.out.println("Shutting Down the Channel...");
        channel.shutdown();
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