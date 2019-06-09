package com.divya.greetings.models;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class RequiredConfigurations {

    public static Set<String> requiredConfigurations() {
        return new HashSet<>(asList(
                "KAFKA_BROKERS",
                "KAFKA_MESSAGE_COUNT",
                "KAFKA_GROUP_ID_CONFIG",
                "GREET_TOPIC_NAME",
                "KAFKA_OFFSET_RESET_LATEST",
                "OFFSET_RESET_EARLIER",
                "DB_HOST",
                "DB_PASSWORD",
                "DB_USERNAME",
                "LEAK_THRESHOLD",
                "DB_MAX_POOL_SIZE",
                "DB_NAME",
                "DB_PORT",
                "GREET_TIMEOUT_IN_SEC"
        ));
    }
}
