package com.ukayunnuo.controller;

import com.ukayunnuo.core.RedisKey;
import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.request.RedisParamReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.commands.JedisCommands;

import javax.annotation.Resource;

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

    @PostMapping("setValue")
    public Result<String> setRedisValue(@RequestBody RedisParamReq req) {

        String redisKey = RedisKey.REDIS_DEMO_KEY.makeRedisKey(req.getKey());
        return Result.success(jedisCommands.setex(redisKey, RedisKey.REDIS_DEMO_KEY.expireTime(), req.getValue()));
    }

    @PostMapping("getValue")
    public Result<String> getRedisValue(@RequestBody RedisParamReq req) {

        return Result.success(jedisCommands.get(RedisKey.REDIS_DEMO_KEY.makeRedisKey(req.getKey())));
    }

}
