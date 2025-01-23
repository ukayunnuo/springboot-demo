package com.ukayunnuo.config;


import com.ukayunnuo.handler.MqttMessageReceiverHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

/**
 * 消费者配置
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
@Configuration
@IntegrationComponentScan
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
public class MqttInboundConfiguration {


    @Resource
    private MqttProperties mqttProperties;

    @Resource
    private MqttConfig mqttConfig;


    private MqttMessageReceiverHandler mqttMessageReceiverHandler;

    public static final String CLIENT_SUFFIX_CONSUMERS = "_consumer";


    /**
     * MQTT信息通道（消费者）
     *
     * @return 消息同道
     */
    @Bean
    public MessageChannel mqttInBoundChannel() {
        // 单线程- Spring Integration默认的消息通道，它允许将消息发送给一个订阅者，然后阻碍发送直到消息被接收。
        // return new DirectChannel();

        // 多线程
        return new ExecutorChannel(Executors.newFixedThreadPool(10));

    }

    /**
     * mqtt入站消息处理器（用于指定消息入站通道接收到生产者生产的消息后处理消息）
     *
     * @return 消息处理器
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInBoundChannel")
    public MessageHandler mqttMessageHandler() {
        return this.mqttMessageReceiverHandler;
    }

    /**
     * MQTT消息订阅绑定（消费者）
     * @return 消息订阅
     */
    @Bean
    public MessageProducerSupport mqttInbound() {
        MqttPahoClientFactory mqttPahoClientFactory = mqttConfig.mqttClientFactory();

        // Paho客户端消息驱动通道适配器，主要用来订阅主题
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                mqttProperties.getClientId() + CLIENT_SUFFIX_CONSUMERS,
                mqttPahoClientFactory,
                mqttProperties.getTopics()
        );
//        adapter.setCompletionTimeout(5000L);
        // Paho消息转换器
        DefaultPahoMessageConverter defaultPahoMessageConverter = new DefaultPahoMessageConverter();
        // 按字节接收消息
        // defaultPahoMessageConverter.setPayloadAsBytes(true);
        adapter.setConverter(defaultPahoMessageConverter);
        // 设置QoS
        adapter.setQos(1);
        // 设置订阅通道
        adapter.setOutputChannel(mqttInBoundChannel());
        return adapter;
    }

}

