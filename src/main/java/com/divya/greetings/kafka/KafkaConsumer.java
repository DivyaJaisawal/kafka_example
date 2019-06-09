package com.divya.greetings.kafka;

import com.gojek.ApplicationConfiguration;
import com.gojek.Figaro;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Arrays;

import static com.divya.greetings.models.RequiredConfigurations.requiredConfigurations;

public class KafkaConsumer {
    public static void main(String[] args) {
        ApplicationConfiguration configuration = Figaro.configure(requiredConfigurations());

        System.out.println("Subscribed to topic " + configuration.getValueAsString("GREET_TOPIC_NAME"));
        KafkaConsumer kafkaConsumer = new KafkaConsumer();
        kafkaConsumer.consumeMessage(configuration);
    }

    public void consumeMessage(ApplicationConfiguration configuration) {
        Consumer<String, String> consumer = KafkaCreator.createConsumer(configuration);
        consumer.subscribe(Arrays.asList(configuration.getValueAsString("GREET_TOPIC_NAME")));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Supplier id= " +
                       record.key() +
                        " Supplier  Name = " + record.value() + " offset = " + record.offset());
            }
        }
    }
}