package com.divya.greetings.factory;

import com.divya.greetings.kafka.KafkaCreator;
import com.divya.greetings.kafka.MessagePublisher;
import com.divya.greetings.repository.GreetRepository;
import com.divya.greetings.service.GreetingService;
import com.gojek.ApplicationConfiguration;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import org.skife.jdbi.v2.DBI;

import java.util.Collections;

public class GreetingServiceFactory {

    public static ServerServiceDefinition instance(ApplicationConfiguration appConfig) {
        final DBI dbi = new DBIFactory(appConfig).create();
        final Integer greetTimeoutInSec =
                appConfig.getValueAsInt("GREET_TIMEOUT_IN_SEC", 1);
        GreetRepository repository = new GreetRepository(dbi, greetTimeoutInSec);
        MessagePublisher kafkaProducer = new MessagePublisher(appConfig, KafkaCreator.createProducer(appConfig));
        DBIFactory dbiFactory = new DBIFactory(appConfig);
        GreetingService greetingService = new GreetingService(repository, dbiFactory, appConfig, kafkaProducer);
        return ServerInterceptors.intercept(greetingService, Collections.emptyList());
    }
}
