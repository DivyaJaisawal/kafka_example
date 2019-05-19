package com.divya.greetings.service;

import com.example.grpc.Greet;
import com.example.grpc.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GreetingServiceTest {
    private static Server inProcessServer;
    private static ManagedChannel inProcessChannel;

    @Before
    public void setUp() throws Exception {
        String serverName = "in-process server for " + GreetingServiceTest.class;
        inProcessServer = InProcessServerBuilder.forName(serverName).
                addService(new GreetingService().bindService()).build();
        inProcessChannel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        inProcessServer.start();

    }

    @After
    public void tearDown() {

        inProcessServer.shutdownNow();
        inProcessChannel.shutdownNow();
    }

    @Test
    public void shouldGreetGreetingGprcIsCalled() {

        GreetingServiceGrpc.GreetingServiceBlockingStub blockingStub = GreetingServiceGrpc.newBlockingStub(inProcessChannel);
        Greet.HelloRequest request = Greet.HelloRequest.newBuilder().setMessage("Hello world").build();
        Greet.HelloResponse actualResponse = blockingStub.greeting(request);
        assertNotNull(actualResponse);
        assertEquals("Hello world", actualResponse.getGreeting());
    }
}
