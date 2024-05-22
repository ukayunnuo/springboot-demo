package com.ukayunnuo.core;

import cn.hutool.core.util.StrUtil;

/**
 * redis key
 *
 * @author yunnuo
 * @since 1.0.0
 */
public enum RedisKey {

    /**
     * node 与 slot 区间映射 mapping
     */
    NODE_SLOT_MAPPING("node_slot:mapping", 180 * 24 * 60 * 60L),
    /**
     * node 与 hashtag key 映射 mapping
     */
    NODE_HASHTAG_KEY_MAPPING("node_hashtag_key:mapping", 180 * 24 * 60 * 60L),

    /**
     * 数据池
     */
    DATA_POOL("data:pool", 180 * 24 * 60 * 60L),

    /**
     * 用户已获取过的pool数据集合
     */
    USER_ACQUIRED_POOL_DATA_LIST("user:acquired:pool_data:list", 30 * 24 * 60 * 60L),

    ;


    private final String prefix;

    private final Long expireTime;

    RedisKey(String prefix, Long expireTime) {
        this.prefix = prefix;
        this.expireTime = expireTime;
    }

    public String getPrefix() {
        return prefix;
    }

    public Long expireTime() {
        return expireTime;
    }

    /**
     * 组装 redis key
     *
     * @param items 参数
     * @return 组装结果
     */
    public String makeRedisKey(Object... items) {
        return makeRedisKey(":", items);
    }

    /**
     * 组装 redis key
     *
     * @param separator 分隔符
     * @param items     参数
     * @return 组装结果
     */
    private String makeRedisKey(String separator, Object... items) {
        return StrUtil.join(separator, this.prefix, items);
    }
}
