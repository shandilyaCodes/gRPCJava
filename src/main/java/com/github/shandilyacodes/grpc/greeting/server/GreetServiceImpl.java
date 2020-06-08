package com.github.shandilyacodes.grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {

        // Extract The Fields We Need
        final Greeting greeting = request.getGreeting();
        final String firstName = greeting.getFirstName();

        // Prepare the Response
        String result = "Hello " + firstName;
        GreetResponse response = GreetResponse.newBuilder().setResult(result).build();

        // Send the Response
        responseObserver.onNext(response);

        // Complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();
        for (int i = 0 ; i < 10 ; i++) {
            String result = "Hello " + firstName + " , Response Number : " + i;
            GreetManyTimesResponse response = GreetManyTimesResponse
                    .newBuilder()
                    .setResult(result)
                    .build();
            responseObserver.onNext(response);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {

            String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                // Client Sends a Message
                result += "Hello! " + value.getGreeting().getFirstName() + "!";
            }

            @Override
            public void onError(Throwable t) {
                // Client Sends an Error
            }

            @Override
            public void onCompleted() {
                // Client is Done!
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }
}