package com.ukayunnuo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.commands.JedisCommands;

import javax.annotation.Resource;

/**
 * @author yunnuo <a href="nuo.he@backgardon.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-15
 */
@Slf4j
@Configuration
public class JedisHandler {

    @Resource
    private RedisProperties properties;

    @Bean
    public JedisCommands jedisCommands(){
        return new Jedis(properties.getHost(), properties.getPort());
    }

}
