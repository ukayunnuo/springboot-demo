package com.ukayunnuo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("spring.mqtt")
public class MqttProperties {

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 连接地址
     */
    private String url;
    /**
     * 客户Id
     */
    private String clientId;
    /**
     * 默认连接话题
     */
    private String defaultTopic;
    /**
     * 超时时间（单位：秒）
     */
    private Integer timeout;
    /**
     * 保持连接数
     */
    private Integer keepalive;

    /**
     * 间隔多长时间发布一组数据 (毫秒)
     */
    private Long millis;

    /**
     * 是否启用mqtt功能
     */
    private Boolean enabled = Boolean.FALSE;

    public String[] getTopics() {
        return defaultTopic.split(",");
    }

    public String[] getServerURIs(){
        return url.split(",");
    }
}