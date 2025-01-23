package com.ukayunnuo.utils;

import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.gateway.MqttGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * mqtt 工具类
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
public class MqttProducerUtil {

    @Resource
    private MqttGateway mqttGateway;


    /**
     * 发送mqtt消息
     *
     * @param topic   主题
     * @param message 内容
     */
    public void send(String topic, String message) {
        mqttGateway.sendToMqtt(topic, message);
    }

    /**
     * 发送mqtt消息
     *
     * @param topic       主题
     * @param qos         消息处理机制
     * @param messageBody 消息体
     */
    public void send(String topic, int qos, String messageBody) {
        mqttGateway.sendToMqtt(topic, qos, messageBody);
    }

    /**
     * 发送mqtt消息
     *
     * @param topic       主题
     * @param qos         消息处理机制
     * @param messageBody 消息体
     */
    public <T> void sendToJsonStr(String topic, int qos, T messageBody) {
        mqttGateway.sendToMqtt(topic, qos, JSONObject.toJSONString(messageBody));
    }

    /**
     * 发送mqtt消息
     *
     * @param topic   主题
     * @param qos     消息处理机制
     * @param message 消息体
     */
    public void send(String topic, int qos, byte[] message) {
        mqttGateway.sendToMqtt(topic, qos, message);
    }

}