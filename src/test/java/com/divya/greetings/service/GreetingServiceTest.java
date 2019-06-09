package com.divya.greetings.service;

import com.divya.greetings.factory.DBIFactory;
import com.divya.greetings.kafka.MessagePublisher;
import com.divya.greetings.repository.GreetRepository;
import com.example.grpc.Greet;
import com.example.grpc.GreetingServiceGrpc;
import com.gojek.ApplicationConfiguration;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class GreetingServiceTest {
    @Mock
    private GreetRepository greetRepository;
    @Mock
    private DBIFactory dbiFactory;
    @Mock
    ApplicationConfiguration appConfig;
    @Mock
    MessagePublisher producer;


    private static Server inProcessServer;
    private static ManagedChannel inProcessChannel;


    @Before
    public void setUp() throws Exception {
        String serverName = "in-process server for " + GreetingServiceTest.class;
        inProcessServer = InProcessServerBuilder.forName(serverName).
                addService(new GreetingService(greetRepository, dbiFactory, appConfig, producer).bindService()).build();
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
