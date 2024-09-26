package com.ukayunnuo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "memcached")
public class MemcachedProperties {

    /**
     * memcached服务器节点
     */
    private String servers;

    /**
     * 开关
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * nio 连接池的数量
     */
    private Integer poolSize;

    /**
     * 设置默认操作超时
     */
    private Long opTimeout;

    /**
     * 是否启用url encode 机制
     */
    private Boolean sanitizeKeys;

}