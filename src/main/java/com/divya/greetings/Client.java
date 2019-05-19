package com.divya.greetings;

import com.example.grpc.Greet;
import com.example.grpc.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private final ManagedChannel channel;
    private final GreetingServiceGrpc.GreetingServiceBlockingStub blockingStub;

    public Client(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build());
    }

    Client(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = GreetingServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        Greet.HelloRequest request = Greet.HelloRequest.newBuilder().setMessage(name).build();

        Greet.HelloResponse response;
        try {
            response = blockingStub.greeting(request);
        } catch (StatusRuntimeException e) {
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 50051);
        try {
            String user = "hello world";
            if (args.length > 0) {
                user = args[0];
            }
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }
}
