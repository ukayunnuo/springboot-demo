package com.ukayunnuo.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 消费者处理器
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
public class MqttMessageReceiverHandler implements MessageHandler {

    /**
     * 消息处理
     *
     * @param message 消息
     * @throws MessagingException 消息异常
     */
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();
        // topic
        String topic = Objects.requireNonNull(headers.get(MqttHeaders.RECEIVED_TOPIC)).toString();
        try {
            // 获取消息体
            String msg;
            if (message.getPayload() instanceof String) {
                msg = message.getPayload().toString();
            } else {
                msg = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
            }
            log.info("mqtt handleMessage 接收到消息 topic:{}, msg：{} ", topic, msg);

        } catch (Exception e) {
            log.error("handleMessage 处理mqtt消息错误! message:{}, e:{}", message, e.getMessage(), e);
        }
    }
}

