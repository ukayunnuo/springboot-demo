package com.ukayunnuo.controller;

import com.ukayunnuo.core.RedisKey;
import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.request.RedisParamReq;
import com.ukayunnuo.domain.response.RedisRes;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.commands.JedisClusterCommands;
import redis.clients.jedis.commands.JedisCommands;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * redis 测试 api 接口
 *
 * @author yunnuo
 * @since 1.0.0
 */
@RestController
@RequestMapping("/demo/redis")
public class RedisDemoController {

    @Resource
    private JedisCommands jedisCommands;

    @Resource
    private JedisClusterCommands jedisClusterCommands;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("setValue/jedisCommands")
    public Result<RedisRes> setRedisValueForJedisCommands(@RequestBody RedisParamReq req) {
        String redisKey = RedisKey.REDIS_DEMO_KEY.makeRedisKey(req.getKey(), "jedisCommands");
        jedisCommands.setex(redisKey, RedisKey.REDIS_DEMO_KEY.expireTime(), req.getValue());
        return Result.success(RedisRes.builder()
                .key(redisKey)
                .value(jedisCommands.get(redisKey))
                .ttl(jedisCommands.ttl(redisKey)).build());
    }

    @PostMapping("setValue/jedisClusterCommands")
    public Result<RedisRes> setRedisValueForJedisClusterCommands(@RequestBody RedisParamReq req) {
        String redisKey = RedisKey.REDIS_DEMO_KEY.makeRedisKey(req.getKey(), "jedisClusterCommands");
        jedisClusterCommands.setex(redisKey, RedisKey.REDIS_DEMO_KEY.expireTime(), req.getValue());
        return Result.success(RedisRes.builder()
                .key(redisKey)
                .value(jedisClusterCommands.get(redisKey))
                .ttl(jedisClusterCommands.ttl(redisKey)).build());
    }

    @PostMapping("setValue/redisTemplate")
    public Result<RedisRes> setRedisValueForRedisTemplate(@RequestBody RedisParamReq req) {
        String redisKey = RedisKey.REDIS_DEMO_KEY.makeRedisKey(req.getKey(), "redisTemplate");
        redisTemplate.opsForValue().set(redisKey, req.getValue(), RedisKey.REDIS_DEMO_KEY.expireTime(), TimeUnit.SECONDS);
        return Result.success(RedisRes.builder()
                .key(redisKey)
                .value(redisTemplate.opsForValue().get(redisKey))
                .ttl(redisTemplate.getExpire(redisKey)).build());
    }

    @PostMapping("setValue/stringRedisTemplate")
    public Result<RedisRes> setRedisValueForStringRedisTemplate(@RequestBody RedisParamReq req) {
        String redisKey = RedisKey.REDIS_DEMO_KEY.makeRedisKey(req.getKey(), "stringRedisTemplate");
        stringRedisTemplate.opsForValue().set(redisKey, req.getValue(), RedisKey.REDIS_DEMO_KEY.expireTime(), TimeUnit.SECONDS);
        return Result.success(RedisRes.builder()
                .key(redisKey)
                .value(stringRedisTemplate.opsForValue().get(redisKey))
                .ttl(stringRedisTemplate.getExpire(redisKey)).build());
    }
}
