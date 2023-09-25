package com.ukayunnuo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * etcd 属性配置
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-25
 */
@Data
@Component
@ConfigurationProperties(prefix = "etcd")
public class EtcdProperties {

    private List<URI> endpoints;


    /**
     * 监听key的前缀
     */
    private String watchKeyPrefix;

}
