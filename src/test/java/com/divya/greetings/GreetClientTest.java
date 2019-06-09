package com.divya.greetings;

import com.example.grpc.Greet;
import com.example.grpc.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GreetClientTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private final GreetingServiceGrpc.GreetingServiceImplBase serviceImpl =
            mock(GreetingServiceGrpc.GreetingServiceImplBase.class, delegatesTo(new GreetingServiceGrpc.GreetingServiceImplBase() {}));
    private GreetClient client;

    @Before
    public void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();

        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(serviceImpl).build().start());

        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        client = new GreetClient(channel);
    }


    @Test
    public void greetMessageDeliveredToServer() {
        ArgumentCaptor<Greet.HelloRequest> requestCaptor = ArgumentCaptor.forClass(Greet.HelloRequest.class);

        client.greet("test name");

        verify(serviceImpl)
                .greeting(requestCaptor.capture(), any());
        assertEquals("test name", requestCaptor.getValue().getMessage());
    }
}