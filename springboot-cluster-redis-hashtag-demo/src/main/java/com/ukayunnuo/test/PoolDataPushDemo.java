package com.ukayunnuo.test;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.enums.PoolType;
import com.ukayunnuo.init.ClusterNodesAndSlotInitHandle;
import com.ukayunnuo.strategy.PoolShardingKeyFactory;
import com.ukayunnuo.strategy.PoolShardingKeyStrategy;
import com.ukayunnuo.utils.PoolShardingKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.commands.JedisClusterCommands;

import javax.annotation.Resource;
import java.util.*;

/**
 * 模拟pool 数据存储到集群redis和同步到各个hashtag key 测试
 *
 * @author yunnuo
 * @date 2024-05-22
 */
@Slf4j
@Component
public class PoolDataPushDemo {

    @Resource
    private PoolShardingKeyFactory poolShardingKeyFactory;

    @Resource
    private JedisClusterCommands jedisClusterCommands;


    // todo: 存储redis数据测试方法，需要开启@PostConstruct
    // @PostConstruct
    public void exec() {
        log.info("PoolDataPushDemo start!");
        long start = System.currentTimeMillis();
        for (PoolType poolType : PoolType.values()) {
            PoolShardingKeyStrategy poolShardingKeyStrategy = poolShardingKeyFactory.getPoolShardingKeyStrategy(poolType);
            Map<String, Set<String>> map = new HashMap<>(5);
            for (int i = 0; i < 10000; i++) {
                // 模拟数据
                String member = RandomUtil.randomString(5);
                // 模拟分数（用于计算分片到那个sharding key中，实现负载均衡）
                int randomScore = RandomUtil.randomInt(0, 10000);
                String shardingKey = poolShardingKeyStrategy.getShardingKey(randomScore);
                if (shardingKey != null) {
                    Set<String> sets = Objects.isNull(map.get(shardingKey)) ? new HashSet<>() : map.get(shardingKey);
                    sets.add(member);
                    map.put(shardingKey, sets);
                }
                if (!map.isEmpty()) {
                    map.forEach((k, v) -> {
                        jedisClusterCommands.sadd(k, v.toArray(new String[0]));
                        jedisClusterCommands.expire(k, poolType.expire);
                        // 数据同步（fixme: 本示例的数据同步采用直接方法调用，仅供测试，实际同步可参考mq消息方案同步）
                        sync(poolType, k);
                    });
                }
            }
        }
        log.info("PoolDataPushDemo end! cost:{}ms", System.currentTimeMillis() - start);
    }

    public void sync(PoolType poolType, String poolShardingKey){
        // 各个节点对应的hashtag 映射
        Map<String, String> map = ClusterNodesAndSlotInitHandle.NODE_HASHTAG_KEY_MAPPING;
        List<String> hashTagRedisKeys = new ArrayList<>();
        // 将sharding key 同步到 各个 hashtag key中
        map.values().forEach(hashtag -> {
            String redisKey = PoolShardingKeyUtil.getHashtagPoolShardingKey(poolShardingKey, hashtag);
            String[] members = jedisClusterCommands.smembers(poolShardingKey).toArray(new String[0]);
            if (members.length == 0){
                return;
            }
            if (jedisClusterCommands.exists(redisKey)){
                jedisClusterCommands.del(redisKey);
                jedisClusterCommands.sadd(redisKey, members);
            }else {
                jedisClusterCommands.sadd(redisKey, members);
            }
            jedisClusterCommands.expire(redisKey, poolType.expire);
            hashTagRedisKeys.add(redisKey);
        });
        log.info("pool data sync poolShardingKey-> {}, poolType-> {}, 同步的对应hashtag keys:{}", poolShardingKey, poolType, JSONObject.toJSONString(hashTagRedisKeys));
    }
}
