package com.ukayunnuo.strategy.impl;

import com.ukayunnuo.core.RedisKey;
import com.ukayunnuo.enums.PoolType;
import com.ukayunnuo.enums.poolsharding.Pool2Sharding;
import com.ukayunnuo.enums.poolsharding.PoolShardingType;
import com.ukayunnuo.strategy.PoolShardingKeyStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * pool_2 池子 分片 策略实现
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-05-22
 */
public class Pool2ShardingKeyStrategy implements PoolShardingKeyStrategy {

    @Override
    public List<String> getShardingKeys() {
        return Arrays.stream(Pool2Sharding.values()).map(item ->
                RedisKey.DATA_POOL.makeRedisKey(PoolType.POOL_2.name, item.type.toString())).collect(Collectors.toList());
    }

    @Override
    public String getShardingKey(Integer score) {
        PoolShardingType sharding = Pool2Sharding.getSharding(score);
        if (sharding == null){
            return null;
        }
        return RedisKey.DATA_POOL.makeRedisKey(PoolType.POOL_2.name, sharding.toString());
    }
}
