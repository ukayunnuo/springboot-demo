package com.ukayunnuo.strategy;


import com.ukayunnuo.core.ErrorCode;
import com.ukayunnuo.core.exception.ServiceException;
import com.ukayunnuo.enums.PoolType;
import com.ukayunnuo.strategy.impl.Pool1ShardingKeyStrategy;
import com.ukayunnuo.strategy.impl.Pool2ShardingKeyStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author yunnuo
 * @date 2024-02-22
 */
@Slf4j
@Component
public class PoolShardingKeyFactory {

    private final Map<PoolType, PoolShardingKeyStrategy> POOL_MAPPING;

    public PoolShardingKeyFactory() {
        POOL_MAPPING = init();
    }

    public HashMap<PoolType, PoolShardingKeyStrategy> init() {
        HashMap<PoolType, PoolShardingKeyStrategy> map = new HashMap<>(PoolType.values().length);
        map.put(PoolType.POOL_1, new Pool1ShardingKeyStrategy());
        map.put(PoolType.POOL_2, new Pool2ShardingKeyStrategy());
        map.put(PoolType.POOL_3, new Pool1ShardingKeyStrategy());
        map.put(PoolType.POOL_4, new Pool1ShardingKeyStrategy());
        map.put(PoolType.POOL_5, new Pool1ShardingKeyStrategy());
        return map;
    }

    public PoolShardingKeyStrategy getPoolShardingKeyStrategy(PoolType poolType) {
        if (!POOL_MAPPING.containsKey(poolType)) {
            log.warn("PoolShardingKeyFactory getPoolShardingKeyStrategy not found mapping PoolType PoolShardingKeyStrategy! PoolType:{}", poolType);
            throw new ServiceException(ErrorCode.PARAM_ERROR.getCode(), "not found mapping PoolType PoolShardingKeyStrategy!");
        }
        return POOL_MAPPING.get(poolType);
    }

}
