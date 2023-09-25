package com.ukayunnuo.config;

import io.etcd.jetcd.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * etcd 配置类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-25
 */
@Configuration
public class EtcdConfig {
    @Resource
    private EtcdProperties etcdProperties;

    @Bean
    public Client etcdClient(){
        return Client.builder()
                .endpoints(etcdProperties.getEndpoints())
                .build();
    }

}
