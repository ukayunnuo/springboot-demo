package com.ukayunnuo.config;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import javax.annotation.Resource;

/**
 * mqtt 配置类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
public class MqttConfig {

    @Resource
    private MqttProperties mqttProperties;


    /**
     * 连接配置
     *
     * @return 配置
     */
    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setServerURIs(mqttProperties.getServerURIs());
        options.setUserName(mqttProperties.getUsername());
        options.setPassword(mqttProperties.getPassword().toCharArray());
        options.setConnectionTimeout(mqttProperties.getTimeout());
        options.setKeepAliveInterval(mqttProperties.getKeepalive());
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(mqttProperties.getKeepalive());
        return options;
    }

    /**
     * 连接工厂
     *
     * @return 工厂
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions mqttConnectOptions = mqttConnectOptions();
        factory.setConnectionOptions(mqttConnectOptions);
        log.info("初始化 MQTT 配置:{}", JSONObject.toJSONString(mqttConnectOptions));
        return factory;
    }

}
