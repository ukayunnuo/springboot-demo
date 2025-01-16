package com.ukayunnuo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

/**
 * 生产者配置
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
public class MqttOutboundConfiguration {

    @Resource
    private MqttProperties mqttProperties;

    @Resource
    private MqttConfig mqttConfig;

    public static final String CLIENT_SUFFIX_PRODUCER = "_producer";

    /**
     * MQTT信息通道（生产者）
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        // 单线程- Spring Integration默认的消息通道，它允许将消息发送给一个订阅者，然后阻碍发送直到消息被接收。
        // return new DirectChannel();

        // 多线程
        return new ExecutorChannel(Executors.newFixedThreadPool(10));
    }

    /**
     * MQTT消息处理器（生产者）
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        // 客户端id
        String clientId = mqttProperties.getClientId();
        // 默认主题
        String defaultTopic = mqttProperties.getDefaultTopic();
        MqttPahoClientFactory mqttPahoClientFactory = mqttConfig.mqttClientFactory();

        // 发送消息和消费消息Channel可以使用相同MqttPahoClientFactory
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + CLIENT_SUFFIX_PRODUCER, mqttPahoClientFactory);
        // true，异步，发送消息时将不会阻塞。
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(defaultTopic);
        // 默认QoS
        messageHandler.setDefaultQos(1);
        // Paho消息转换器
        DefaultPahoMessageConverter defaultPahoMessageConverter = new DefaultPahoMessageConverter();
        // defaultPahoMessageConverter.setPayloadAsBytes(true);
        // 发送默认按字节类型发送消息
        messageHandler.setConverter(defaultPahoMessageConverter);
        return messageHandler;
    }

}
