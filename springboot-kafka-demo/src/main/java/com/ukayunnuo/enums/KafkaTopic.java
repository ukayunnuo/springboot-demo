package com.ukayunnuo.enums;

import lombok.Getter;

/**
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-12-28
 */
@Getter
public enum KafkaTopic {

    /**
     * 使用默认配置的 ContainerFactory 进行监听 (single 模式) <b>注意：需要设置 spring.kafka.listener.type: single</b>
     */
    TOPIC_TEST_SINGLE_MODEL("default_container_factory-test.kafka.demo.default.single", ""),

    /**
     * 使用默认配置的 ContainerFactory 进行监听 (batch 模式) <b>注意：需要设置 spring.kafka.listener.type: batch</b>
     */
    TOPIC_TEST_BATCH_MODEL("default_container_factory-test.kafka.demo.default.batch", "使用默认配置的 ContainerFactory 进行监听 (batch 模式)"),

    /**
     * 使用自定义的 batchContainerFactory 进行监听
     */
    TOPIC_TEST_CUSTOM_BATCH_MODEL("batch_container_factory-test.kafka.demo.batch", "使用自定义的 batchContainerFactory 进行监听");

    /**
     * topic 主题
     */
    public final String topic;

    /**
     * 备注
     */
    private final String remark;

    KafkaTopic(String topic, String remark) {
        this.topic = topic;
        this.remark = remark;
    }

}
