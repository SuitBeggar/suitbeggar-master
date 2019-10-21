package com.common.kafka;

import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

import java.util.*;

/**
 * Created by fangyitao on 2019/8/16.
 * kafka消费者
 */
public class KafkaConsumerUtil {
    private final KafkaConsumer<String, String> consumer;

    private KafkaConsumerUtil() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.133.232.75:9092,10.133.232.76:9092,10.133.232.77:9092,10.133.232.79:9092,10.133.232.80:9092,10.133.232.81:9092,10.133.232.82:9092,10.133.232.83:9092,10.133.232.84:9092");
        props.put("group.id", "0168tests");
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<>(props);
    }

    public void consume() {

        consumer.subscribe(Arrays.asList("dm_nx_bulletin_test"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records){
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }

        }

    }

    public static void main(String[] args) {
        new KafkaConsumerUtil().consume();
    }
}
