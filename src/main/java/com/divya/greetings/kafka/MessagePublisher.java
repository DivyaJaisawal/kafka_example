package com.divya.greetings.kafka;

import com.gojek.ApplicationConfiguration;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class MessagePublisher {
    private static final Logger logger = Logger.getLogger(MessagePublisher.class.getName());
    private final ApplicationConfiguration configuration;
    private final Producer kafkaProducer;


    public MessagePublisher(ApplicationConfiguration configuration,
                            Producer kafkaProducer) {
        this.configuration = configuration;
        this.kafkaProducer = kafkaProducer;
    }

    public void publishMessage(String key, String msg) {
        try {
            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>(configuration.getValueAsString(
                            "GREET_TOPIC_NAME"), key, msg);
            Future<RecordMetadata> future = kafkaProducer.send(producerRecord);
            if (future.get() != null) {
                logger.info("Publishing to kafka is successful. Message: " + msg);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.info("Publishing to kafka failed due to InterruptedException. " +
                    "Message: " + msg);
        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.info("Publishing to kafka failed due to ExecutionException" + msg);
        }
    }
}