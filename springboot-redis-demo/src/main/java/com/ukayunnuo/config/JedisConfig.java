package com.ukayunnuo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.commands.JedisClusterCommands;
import redis.clients.jedis.commands.JedisCommands;

import javax.annotation.Resource;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * redis 配置类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-15
 */
@Slf4j
@Configuration
public class JedisConfig {

    @Resource
    private RedisProperties properties;

    @Bean
    public JedisCommands jedisCommands() {
        return new Jedis(properties.getHost(), properties.getPort(), properties.isSsl());
    }

    @Bean
    public JedisClusterCommands jedisClusterCommands() {
        Set<HostAndPort> nodes = properties.getCluster().getNodes().stream().map(item -> {
            String[] split = item.split(":");
            return new HostAndPort(split[0], Integer.parseInt(split[1]));
        }).collect(Collectors.toSet());

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(properties.getLettuce().getPool().getMaxActive());
        config.setMaxIdle(properties.getLettuce().getPool().getMaxIdle());
        config.setMaxWait(properties.getLettuce().getPool().getMaxWait());

        return new JedisCluster(nodes, config);
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置序列化方式
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());

        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
