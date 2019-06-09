package com.divya.greetings;

import com.divya.greetings.kafka.KafkaCreator;
import com.divya.greetings.kafka.MessagePublisher;
import com.example.grpc.Greet;
import com.gojek.ApplicationConfiguration;
import com.gojek.Figaro;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Parser;
import com.gopay.kafka.KafkaConsumerClient;
import com.gopay.kafka.ProtobufMessageReader;
import com.jayway.awaitility.Awaitility;
import kafka.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.mockito.Mock;

import java.util.*;
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

        //start consumer in another thread
        //consume message and add to list

        messagePublisher.publishMessage(greet.getMessage(), greet.getMessage());
        List<String> message = startConsumer(messageList);

        assertEquals("hello world", message.get(0));

        //wait until message list size is 1
        //assert on the consumed list with the message being published


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
                                        " Supplier  Name = " + record.value() + " offset = " + record.offset());
                                messageList.add(record.value());
                            }

                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return messageList;
    }

    public void waitForConsumer(String consumerGroupID) {
        Properties map = new Properties();
        map.put("bootstrap.servers", this.kafkaCluster.bootstrapServers());
        AdminClient adminClient = AdminClient.create(map);
        Awaitility.await().atMost(1000, TimeUnit.SECONDS).until(() -> {
            try {
                return (adminClient.describeConsumerGroup("GREET_TOPIC_NAME").consumers().get()).size() > 0;

            } catch (Exception var3) {
                return false;
            }
        });
    }

    public <T extends AbstractMessage> List<T> waitForProtoMessages(int numMessages, String topic, Parser<T> protoParser) {
        Properties consumerConfig = KafkaConsumerClient.kafkaProperties(this.config, "waitForProtoMessages");
        consumerConfig.setProperty("auto.offset.reset", "earliest");
        KafkaConsumer consumer = new KafkaConsumer(consumerConfig);
        consumer.subscribe(Collections.singleton(topic));
        KafkaConsumerClient consumerClient = new KafkaConsumerClient(consumer, new ProtobufMessageReader(protoParser));
        List<AbstractMessage> messages = new ArrayList();
        Executors.newSingleThreadExecutor().execute(() -> {
            consumerClient.start((m) -> {
                messages.add((AbstractMessage)m);
            });
        });
        Awaitility.await().atMost(1000, TimeUnit.SECONDS).until(() -> {
            return messages.size() >= numMessages;
        });
        return (List<T>) messages;
    }

}
