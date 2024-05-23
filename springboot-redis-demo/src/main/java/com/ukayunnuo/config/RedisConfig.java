package com.ukayunnuo.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.*;
import redis.clients.jedis.commands.JedisClusterCommands;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.exceptions.JedisDataException;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
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
public class RedisConfig {

    public static final int DEFAULT_MAX_ATTEMPTS = 5;

    @Resource
    private RedisProperties properties;


    private JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisProperties.Lettuce lettuce = properties.getLettuce();
        RedisProperties.Jedis jedis = properties.getJedis();
        if (Objects.nonNull(lettuce) && Objects.nonNull(lettuce.getPool())) {
            config.setMaxTotal(lettuce.getPool().getMaxActive());
            config.setMaxIdle(lettuce.getPool().getMaxIdle());
            config.setMaxWait(lettuce.getPool().getMaxWait());
            return config;
        }
        if (Objects.nonNull(jedis) && Objects.nonNull(jedis.getPool())) {
            config.setMaxTotal(jedis.getPool().getMaxActive());
            config.setMaxIdle(jedis.getPool().getMaxIdle());
            config.setMaxWait(jedis.getPool().getMaxWait());
            return config;
        }
        return config;
    }

    @Lazy
    @Bean
    public JedisCommands jedisCommands() {
        try {
            if (StrUtil.isNotBlank(properties.getHost())) {
                return new Jedis(new HostAndPort(properties.getHost(), properties.getPort()),
                        DefaultJedisClientConfig.builder()
                                .connectionTimeoutMillis((int) properties.getTimeout().toMillis())
                                .password(properties.getPassword())
                                .database(properties.getDatabase())
                                .ssl(properties.isSsl()).build()
                );
            }
        } catch (Exception e) {
            log.error("redis jedisCommands init properties:{}, error:{}", JSONObject.toJSONString(properties),  e.getMessage(), e);
        }
        return null;
    }

    @Lazy
    @Bean
    public JedisClusterCommands jedisClusterCommands() {
        return jedisCluster();
    }

    @Lazy
    @Bean
    public JedisCluster jedisCluster() {
        RedisProperties.Cluster cluster = properties.getCluster();
        JedisCluster jedisCluster;
        Set<HostAndPort> nodes;
        try {
            if (Objects.isNull(cluster)) {
                if (StrUtil.isBlank(properties.getHost())) {
                    throw new RedisConnectionFailureException("redis config param deficiency! Verify the configuration and try again.");
                }
                nodes = new HashSet<>(Collections.singleton(new HostAndPort(properties.getHost(), properties.getPort())));
            } else {
                nodes = cluster.getNodes().stream().map(item -> {
                    String[] split = item.split(":");
                    return new HostAndPort(split[0], Integer.parseInt(split[1]));
                }).collect(Collectors.toSet());
            }
            jedisCluster = assembleJedisCluster(nodes);
            log.info("RedisConfig --> jedisCluster init... nodes:{}, config:{}", JSONObject.toJSONString(nodes), JSONObject.toJSONString(getJedisPoolConfig()));
        } catch (JedisDataException e) {
            log.error("RedisConfig --> new JedisCluster error! e:{}", e.getMessage());
            return null;
        }
        return jedisCluster;
    }

    public JedisCluster assembleJedisCluster(Set<HostAndPort> nodes) {
        JedisPoolConfig config = getJedisPoolConfig();
        if (StrUtil.isBlank(properties.getPassword())) {
            return new JedisCluster(nodes,
                    (int) properties.getTimeout().getSeconds() * 1000,
                    DEFAULT_MAX_ATTEMPTS, config);
        }
        return new JedisCluster(nodes,
                (int) properties.getTimeout().getSeconds() * 1000,
                (int) properties.getTimeout().getSeconds() * 1000,
                DEFAULT_MAX_ATTEMPTS, properties.getPassword(),
                config);
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
