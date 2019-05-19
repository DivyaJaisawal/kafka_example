package com.divya.greetings.service;

import com.divya.greetings.factory.DBIFactory;
import com.divya.greetings.models.Greet;
import com.divya.greetings.repository.GreetRepository;
import com.example.grpc.Greet.HelloRequest;
import com.example.grpc.Greet.HelloResponse;
import com.example.grpc.GreetingServiceGrpc.GreetingServiceImplBase;
import io.grpc.stub.StreamObserver;

public class GreetingService extends GreetingServiceImplBase {
    private GreetRepository repository;
    private DBIFactory dbiFactory;

    public GreetingService(GreetRepository repository, DBIFactory dbiFactory) {
        this.repository = repository;
        this.dbiFactory = dbiFactory;
    }

    @Override
    public void greeting(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse.Builder builder = HelloResponse.newBuilder();
        Greet message = new Greet(req.getMessage());
        repository.saveGreetings(message);
        builder.setGreeting(req.getMessage());
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
