package com.ukayunnuo.strategy.impl;

import com.ukayunnuo.core.RedisKey;
import com.ukayunnuo.enums.poolsharding.Pool5Sharding;
import com.ukayunnuo.enums.poolsharding.PoolShardingType;
import com.ukayunnuo.strategy.PoolShardingKeyStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * pool_5 池子 分片 策略实现
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-05-22
 */
public class Pool5ShardingKeyStrategy implements PoolShardingKeyStrategy {

    @Override
    public List<String> getShardingKeys() {
        return Arrays.stream(Pool5Sharding.values()).map(item ->
                RedisKey.DATA_POOL.makeRedisKey(item.type.toString())).collect(Collectors.toList());
    }

    @Override
    public String getShardingKey(Integer score) {
        PoolShardingType sharding = Pool5Sharding.getSharding(score);
        if (sharding == null){
            return null;
        }
        return RedisKey.DATA_POOL.makeRedisKey(sharding.toString());
    }
}
