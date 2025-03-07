package com.ukayunnuo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * nacos 动态配置更新测试配置
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Data
@RefreshScope // 动态刷新配置
@Component
@ConfigurationProperties(prefix = "test")
public class TestDynamicConfig {

    private String name;

    private Double version;

}
