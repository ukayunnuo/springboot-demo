package com.ukayunnuo.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Objects;


/**
 * kafka配置类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties({KafkaProperties.class})
@EnableKafka
@AllArgsConstructor
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;

    private static final Integer DEFAULT_PARTITION_NUM = 3;

    private static final String GROUP_ID = "springboot-kafka-demo-batch-manual_immediate";

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // 提交模式
        ContainerProperties.AckMode ackMode = Objects.nonNull(kafkaProperties.getListener().getAckMode()) ? kafkaProperties.getListener().getAckMode() : ContainerProperties.AckMode.MANUAL_IMMEDIATE;
        factory.getContainerProperties().setAckMode(ackMode);

        // 分区
        Integer concurrency = kafkaProperties.getListener().getConcurrency();
        concurrency = (Objects.nonNull(concurrency) && concurrency > 0) ? concurrency : DEFAULT_PARTITION_NUM;
        factory.setConcurrency(concurrency);

        // 拉取类型：单个/批量
        KafkaProperties.Listener.Type type = kafkaProperties.getListener().getType();
        factory.setBatchListener(Objects.equals(type, KafkaProperties.Listener.Type.BATCH));

        return factory;
    }



    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> batchContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setGroupId(GROUP_ID);
        factory.setConcurrency(DEFAULT_PARTITION_NUM);
        factory.setBatchListener(true);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

}
