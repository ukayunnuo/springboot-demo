package com.ukayunnuo.config;

import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * memcached配置类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class MemcachedConfig {

    @Resource
    private MemcachedProperties memcachedProperties;

    @Bean
    public MemcachedClient memcachedClient() throws Exception {
        MemcachedClient memcachedClient = null;
        if (memcachedProperties.getEnable()) {
            try {
                MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(memcachedProperties.getServers()));
                builder.setSanitizeKeys(memcachedProperties.getSanitizeKeys());
                builder.setConnectionPoolSize(memcachedProperties.getPoolSize());
                builder.setOpTimeout(memcachedProperties.getOpTimeout());
                builder.setFailureMode(false);
                builder.setSessionLocator(new KetamaMemcachedSessionLocator());
                builder.setCommandFactory(new BinaryCommandFactory());
                memcachedClient = builder.build();
            } catch (IOException e) {
                log.error("memcachedClient build error! case: {}", e.getMessage(), e);
                throw e;
            }
        }
        return memcachedClient;
    }
}