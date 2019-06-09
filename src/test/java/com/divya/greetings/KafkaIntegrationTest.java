package com.divya.greetings;

import com.divya.greetings.kafka.KafkaConsumer;
import com.example.grpc.Greet;
import com.gojek.ApplicationConfiguration;
import com.gojek.Figaro;
import com.gopay.kafka.testutil.KafkaUtil;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.util.List;
import java.util.concurrent.Executors;

import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;

public class KafkaIntegrationTest {
    @Rule
    public final EnvironmentVariables env = new EnvironmentVariables();
    private ApplicationConfiguration config;
    private KafkaUtil kafkaUtil;

    @Rule
    public EmbeddedKafkaCluster kafkaCluster = new EmbeddedKafkaCluster(1);

    @Before
    public void setUp() throws Exception {
        env.set("KAFKA_BOOTSTRAP_SERVERS", kafkaCluster.bootstrapServers());
        config = Figaro.configure(emptySet());
        kafkaUtil = new KafkaUtil(config, kafkaCluster, 180);
        kafkaCluster.createTopic(config.getValueAsString("GREET_TOPIC_NAME"), 1, 1);
    }

    @Test
    public void testPublish() throws Exception {
        startConsumer(config.getValueAsString("PROCESSED_JOURNAL_LOGS_CONSUMER_GROUP_ID"));
        kafkaUtil.sendToTopic("GREET_TOPIC_NAME", Greet.HelloRequest.newBuilder().setMessage("helo_world").build());
        List<Greet.HelloResponse> helloResponseList = kafkaUtil.waitForProtoMessages(2, config.getValueAsString("GREET_TOPIC_NAME"), Greet.HelloResponse.parser());

        Greet.HelloResponse helloResponse = helloResponseList.get(0);

        assertEquals("hello_world", helloResponse.getGreeting());
    }

    private void startConsumer(String consumerGroup) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                KafkaConsumer.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        kafkaUtil.waitForConsumer(consumerGroup);
    }
}
