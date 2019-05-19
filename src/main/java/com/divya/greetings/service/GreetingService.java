package com.divya.greetings.service;

import com.example.grpc.Greet.HelloRequest;
import com.example.grpc.Greet.HelloResponse;
import com.example.grpc.GreetingServiceGrpc.GreetingServiceImplBase;
import io.grpc.stub.StreamObserver;

public class GreetingService extends GreetingServiceImplBase {

    @Override
    public void greeting(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse.Builder builder = HelloResponse.newBuilder();
        builder.setGreeting(req.getMessage());
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
