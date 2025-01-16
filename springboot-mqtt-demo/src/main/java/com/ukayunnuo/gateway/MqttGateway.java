package com.ukayunnuo.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * 生产者处理器
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {

    /**
     * 发送mqtt消息
     *
     * @param topic   主题
     * @param payload 消息体 字符串
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

    /**
     * 发送包含qos的消息
     *
     * @param topic   主题
     * @param qos     消息处理机制
     * @param payload 消息体 字符串
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);

    /**
     * 发送包含qos的消息
     *
     * @param topic   主题
     * @param qos     消息处理机制
     * @param payload 消息体 字节
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, byte[] payload);

}
