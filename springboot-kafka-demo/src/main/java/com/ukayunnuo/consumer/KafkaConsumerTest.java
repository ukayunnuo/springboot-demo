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


    /**
     * 使用默认配置的 ContainerFactory 进行监听 (single 模式)
     *
     * @param record        消息
     * @param acknowledgment 提交器
     */
    @KafkaListener(topics = {"default_container_factory-test.kafka.demo.default.single"})
    public void receiveMessageDefaultSingle(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        log.info("receiveMessageDefault-single record key:{}, value:{}", record.key(), record.value());
        acknowledgment.acknowledge();
    }

    /**
     * 使用默认配置的 ContainerFactory 进行监听 (batch 模式)
     *
     * @param records        消息
     * @param acknowledgment 提交器
     */
    @KafkaListener(topics = {"default_container_factory-test.kafka.demo.default.batch"})
    public void receiveMessageDefaultBatch(ConsumerRecords<String, String> records, Acknowledgment acknowledgment) {
        log.info("receiveMessageDefault-batch records size:{}", records.count());
        for (ConsumerRecord<String, String> record : records) {
            log.info("receiveMessageDefault-batch record key:{}, value:{}", record.key(), record.value());
        }
        acknowledgment.acknowledge();
    }

    /**
     * 使用自定义的 batchContainerFactory 进行监听
     *
     * @param records        消息
     * @param acknowledgment 提交器
     */
    @KafkaListener(topics = {"batch_container_factory-test.kafka.demo.batch"}, containerFactory = "batchContainerFactory")
    public void receiveMessage(ConsumerRecords<String, String> records, Acknowledgment acknowledgment) {
        log.info("receiveMessage records size:{}", records.count());
        for (ConsumerRecord<String, String> record : records) {
            log.info("receiveMessage record key:{}, value:{}", record.key(), record.value());
        }
        acknowledgment.acknowledge();
    }


}
