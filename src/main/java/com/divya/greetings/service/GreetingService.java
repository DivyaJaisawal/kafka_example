package com.divya.greetings.service;

import com.divya.greetings.kafka.MessagePublisher;
import com.divya.greetings.models.Greet;
import com.divya.greetings.repository.GreetRepository;
import com.example.grpc.Greet.HelloRequest;
import com.example.grpc.Greet.HelloResponse;
import com.example.grpc.GreetingServiceGrpc.GreetingServiceImplBase;
import io.grpc.stub.StreamObserver;

public class GreetingService extends GreetingServiceImplBase {
    private GreetRepository repository;
    private MessagePublisher producer;

    public GreetingService(GreetRepository repository,
                           MessagePublisher producer) {
        this.repository = repository;
        this.producer = producer;
    }

    @Override
    public void greeting(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse.Builder builder = HelloResponse.newBuilder();
        builder.setGreeting(req.getMessage());
        Greet greet = new Greet(req.getMessage());
        repository.saveGreetings(greet);
        producer.publishMessage(greet.getMessage(), greet.getMessage());
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
