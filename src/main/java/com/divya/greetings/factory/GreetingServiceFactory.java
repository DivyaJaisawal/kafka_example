package com.divya.greetings.factory;

import com.divya.greetings.repository.GreetRepository;
import com.divya.greetings.service.GreetingService;
import com.gojek.ApplicationConfiguration;
import com.gojek.Figaro;
import com.sun.javafx.collections.UnmodifiableListSet;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import org.skife.jdbi.v2.DBI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GreetingServiceFactory {

    public static ServerServiceDefinition instance(ApplicationConfiguration applicationConfiguration, boolean enableMetricsReporting) {
        final ApplicationConfiguration appConfig =
                Figaro.configure(new UnmodifiableListSet<>(getMandatoryVariables()));
        final DBI dbi = new DBIFactory(appConfig).create();
        final Integer greetTimeoutInSec =
                appConfig.getValueAsInt("GREET_TIMEOUT_IN_SEC", 1);
        GreetRepository repository = new GreetRepository(dbi, greetTimeoutInSec);
        DBIFactory dbiFactory = new DBIFactory(appConfig);
        GreetingService greetingService = new GreetingService(repository, dbiFactory);
        return ServerInterceptors.intercept(greetingService, Collections.emptyList());
    }

    private static List<String> getMandatoryVariables() {
        return Arrays.asList("DB_HOST",
                "DB_PASSWORD",
                "DB_USERNAME",
                "LEAK_THRESHOLD",
                "DB_MAX_POOL_SIZE",
                "DB_NAME",
                "DB_PORT",
                "GREET_TIMEOUT_IN_SEC");
    }

}
