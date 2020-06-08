package com.github.shandilyacodes.grpc.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        // Unary
//        SumRequest request = SumRequest
//                .newBuilder()
//                .setFirstNumber(10)
//                .setSecondNumber(25)
//                .build();
//
//        SumResponse response = stub.sum(request);
//        System.out.println(request.getFirstNumber() + " + " + request.getSecondNumber() + " = " + response.getSumResult());

        // Streaming Server
        int number = 56789;

        stub.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder().setNumber(number).build())
                .forEachRemaining(res -> System.out.println(res.getPrimeFactor()));
    }
}