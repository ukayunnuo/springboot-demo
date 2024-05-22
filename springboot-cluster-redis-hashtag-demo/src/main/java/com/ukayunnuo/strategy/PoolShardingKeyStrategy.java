package com.ukayunnuo.strategy;


import java.util.List;

/**
 * pool 数据池 分片策略（实现负载均衡）
 *
 * @author yunnuo
 * @date 2024-05-22
 */
public interface PoolShardingKeyStrategy {

    /**
     * 获取 分片 key
     * @return 返回分片 key
     */
    List<String> getShardingKeys();

    /**
     * 通过分数获取指定的数据池分片类型
     * @param score 分数
     * @return 数据池分片类型
     */
    String getShardingKey(Integer score);

}
