package com.divya.greetings;

import com.divya.greetings.kafka.KafkaCreator;
import com.divya.greetings.kafka.MessagePublisher;
import com.example.grpc.Greet;
import com.gojek.ApplicationConfiguration;
import com.gojek.Figaro;
import com.jayway.awaitility.Awaitility;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;

public class GreetKafkaIntegrationTest {
    @Rule
    public EmbeddedKafkaCluster kafkaCluster = new EmbeddedKafkaCluster(1);

    @Rule
    public final EnvironmentVariables env = new EnvironmentVariables();
    @Mock
    private MessagePublisher messagePublisher;
    private List<String> messageList;
    private ApplicationConfiguration config;

    @Before
    public void setUp() throws Exception {
        env.set("KAFKA_BOOTSTRAP_SERVERS", kafkaCluster.bootstrapServers());
        config = Figaro.configure(emptySet());
        kafkaCluster.createTopic(config.getValueAsString("GREET_TOPIC_NAME"), 1, 1);
        messagePublisher = new MessagePublisher(config, KafkaCreator.createProducer(config));
    }

    @Test
    public void testGreetings() {
        Greet.HelloResponse builder = Greet.HelloResponse.newBuilder().setGreeting("hello world").build();
        com.divya.greetings.models.Greet greet = new com.divya.greetings.models.Greet(builder.getGreeting());
        messagePublisher.publishMessage(greet.getMessage(), greet.getMessage());
        List<String> message = startConsumer(Collections.singletonList("hello"));

        assertEquals("hello", message.get(0));
    }

    private List<String> startConsumer(List<String> messageList) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Consumer<String, String> consumer = KafkaCreator.createConsumer(config);
                consumer.subscribe(Arrays.asList(config.getValueAsString("GREET_TOPIC_NAME")));
                Awaitility.await().atMost(1000, TimeUnit.SECONDS).until(() -> {
                    ConsumerRecords<String, String> records = consumer.poll(100);

                    for (ConsumerRecord<String, String> record : records) {
                        System.out.println("Supplier id= " +
                                record.key() +
                                " Supplier  Name = " + record.value() + " offset = "
                                + record.offset());
                        messageList.add(record.value());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return messageList;
    }
}
