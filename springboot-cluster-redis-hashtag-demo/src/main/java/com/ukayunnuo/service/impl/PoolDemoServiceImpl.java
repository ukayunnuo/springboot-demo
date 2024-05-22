package com.ukayunnuo.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ukayunnuo.core.RedisKey;
import com.ukayunnuo.enums.PoolType;
import com.ukayunnuo.handle.SlotHashTagHandler;
import com.ukayunnuo.service.PoolDemoService;
import com.ukayunnuo.strategy.PoolShardingKeyFactory;
import com.ukayunnuo.strategy.PoolShardingKeyStrategy;
import com.ukayunnuo.utils.PoolShardingKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.commands.JedisClusterCommands;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * pool demo 实现类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-05-22
 */
@Slf4j
@Service
public class PoolDemoServiceImpl implements PoolDemoService {


    @Resource
    private PoolShardingKeyFactory factory;

    @Resource
    private SlotHashTagHandler slotHashTagHandler;

    @Resource
    private JedisClusterCommands jedisClusterCommands;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final Integer USER_MAX_PULL_LIMIT = 20;


    @Override
    public Set<String> getPoolData(Long uid) {
        Set<String> poolData = new HashSet<>();

        // 通过uid计算slot 找到对应的hashtag
        String hashTagKey = slotHashTagHandler.getHashTag(uid);

        // 获取用户已获取的pool数据
        String userAcquiredPoolDataListKey = RedisKey.USER_ACQUIRED_POOL_DATA_LIST.makeRedisKey(uid, hashTagKey);

        try {
            // 对池子按照枚举类型顺序获取
            for (PoolType poolType : PoolType.values()) {
                PoolShardingKeyStrategy poolShardingKeyStrategy = factory.getPoolShardingKeyStrategy(poolType);
                Set<String> tempData = sdiffPoolData(poolType, poolShardingKeyStrategy, hashTagKey, userAcquiredPoolDataListKey, poolData.size());
                if (CollUtil.isNotEmpty(tempData)) {
                    poolData.addAll(tempData);
                }
                if (poolData.size() >= USER_MAX_PULL_LIMIT) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("getPoolData error! uid:{}, e:{}", uid, e.getMessage(), e);
            // 移除本次用户累计拉取数据
            if (CollUtil.isNotEmpty(poolData)) {
                jedisClusterCommands.srem(userAcquiredPoolDataListKey, poolData.toArray(new String[0]));
            }
            throw e;
        }
        return poolData;
    }

    /**
     * 对数据池和用户累计拉取数据集合进行sdiff操作
     *
     * @param poolType 池子类型
     * @param poolShardingKeyStrategy 池子分片key策略
     * @param hashTagKey hashtag key
     * @param userAcquiredPoolDataListKey 用户累计拉取数据集合 redis key
     * @param size 当前用户累计拉取数据数量
     * @return 当前池子进行diff操作后的数据集合
     */
    private Set<String> sdiffPoolData(PoolType poolType, PoolShardingKeyStrategy poolShardingKeyStrategy, String hashTagKey, String userAcquiredPoolDataListKey, int size) {
        Set<String> res = new HashSet<>();

        // 按照各个池子进行分片后的redis存在的 hashTagKey
        List<String> redisKeys = poolShardingKeyStrategy.getShardingKeys().stream()
                .map(poolShardingKey -> PoolShardingKeyUtil.getHashtagPoolShardingKey(poolShardingKey, hashTagKey))
                .filter(jedisClusterCommands::exists).collect(Collectors.toList());

        // 进行diff操作
        for (String redisKey : redisKeys) {
            Optional.ofNullable(redisTemplate.opsForSet().difference(redisKey, userAcquiredPoolDataListKey)).ifPresent(res::addAll);
            if (res.size() >= poolType.pullNumberLimit) {
                break;
            }
        }
        int limit = Math.min(poolType.pullNumberLimit, USER_MAX_PULL_LIMIT - size);

        // 随机获取指定个数的数据
        BiFunction<Set<String>, Integer, Set<String>> shuffleFunction = (set, numValues) -> set.stream().limit(Math.min(numValues, set.size()))
                .collect(Collectors.toSet());

        if (res.isEmpty() || res.size() <= limit) {
            return res;
        }

        return shuffleFunction.apply(res, limit);
    }


}
