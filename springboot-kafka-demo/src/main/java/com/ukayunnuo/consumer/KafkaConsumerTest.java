package com.ukayunnuo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * kafka 消费
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-12-27
 */
@Slf4j
@Component
public class KafkaConsumerTest {


    @KafkaListener(topics = {"test.kafka.demo"})
    public void receiveMessage(ConsumerRecords<String, String> records, Acknowledgment acknowledgment){
        log.info("receiveMessage records size:{}", records.count());
        for (ConsumerRecord<String, String> record : records) {
            log.info("receiveMessage record key:{}, value:{}", record.key(), record.value());
        }
        acknowledgment.acknowledge();
    }


}
