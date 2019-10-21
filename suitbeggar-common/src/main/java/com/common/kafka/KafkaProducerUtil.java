package com.common.kafka;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by fangyitao on 2019/8/16.
 * kafka生产者
 */
public class KafkaProducerUtil {

    private final KafkaProducer<String, String> producer;
    public final static String TOPIC = "TEST-TOPIC";

    private KafkaProducerUtil(){
        Properties props = new Properties();
        //此处配置的是kafka的端口
        //props.put("metadata.broker.list", "127.0.0.1:9092");
        //prps.put("10.133.232.75:9092,10.133.232.76:9092,10.133.232.77:9092,10.133.232.79:9092,10.133.232.80:9092,10.133.232.81:9092,10.133.232.82:9092,10.133.232.83:9092,10.133.232.84:9092");
        props.put("bootstrap.servers", "127.0.0.1:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        //配置value的序列化类
        //props.put("serializer.class", "org.apache.kafka.common.serialization.StringSerializer");
        //配置key的序列化类
        //props.put("key.serializer.class", "org.apache.kafka.common.serialization.StringSerializer");

        //request.required.acks
        //0, which means that the producer never waits for an acknowledgement from the broker (the same behavior as 0.7). This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
        //1, which means that the producer gets an acknowledgement after the leader replica has received the data. This option provides better durability as the client waits until the server acknowledges the request as successful (only messages that were written to the now-dead leader but not yet replicated will be lost).
        //-1, which means that the producer gets an acknowledgement after all in-sync replicas have received the data. This option provides the best durability, we guarantee that no messages will be lost as long as at least one in sync replica remains.
        props.put("request.required.acks","-1");

        producer = new KafkaProducer<String, String>(props);
    }

    void produce() {
        int messageNo = 100;
        final int COUNT = 1000;

        while (messageNo < COUNT) {
            String key = String.valueOf(messageNo);
            String data = "hello kafka message " + key;
            producer.send(new ProducerRecord<String, String>(TOPIC, key ,data));
            System.out.println(data);
            messageNo ++;
        }
    }

    public static void main( String[] args )
    {
        new KafkaProducerUtil().produce();
    }

}
