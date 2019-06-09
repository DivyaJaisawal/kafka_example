package com.divya.greetings;
import com.divya.greetings.factory.DBIFactory;
import com.divya.greetings.factory.GreetingServiceFactory;
import com.divya.greetings.models.RequiredConfigurations;
import com.gojek.ApplicationConfiguration;
import com.gojek.Figaro;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class GreetServer {
    private static final Logger logger = Logger.getLogger(GreetServer.class.getName());
    private io.grpc.Server server;

    private void start() throws IOException {
            int port = 50051;
        ApplicationConfiguration appConfig = Figaro.configure(RequiredConfigurations.requiredConfigurations());
        server = ServerBuilder.forPort(port)
                .addService(GreetingServiceFactory.instance(appConfig))
                .build()
                .start();
        logger.info("GreetServer started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                GreetServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
            DBIFactory.close();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final GreetServer greetServer = new GreetServer();
        greetServer.start();
        greetServer.blockUntilShutdown();
    }
}


