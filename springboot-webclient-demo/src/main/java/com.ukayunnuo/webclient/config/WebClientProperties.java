package com.ukayunnuo.webclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * WebClient 参数类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "webclient")
public class WebClientProperties {

    private Integer connectTimeout;

    private Integer responseTimeout;

    private Map<String, String> serverBaseUrls;

}
