package com.common.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by fangyitao on 2019/8/16.
 * kafka消息监听
 */

@Component
public class KafkaDataListener {
    private Logger log = LoggerFactory.getLogger(KafkaDataListener.class);


    /**
     * 实时获取kafka数据(生产一条，监听生产topic自动消费一条)
     * @param record
     * @throws IOException
     */
    @KafkaListener(topics = {"${kafka.consumer.topic}"})
    public void listen(ConsumerRecord<?, ?> record) throws IOException {
        String value = (String) record.value();
        System.out.println(value);
    }
}
