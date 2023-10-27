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
     * redis demo key 过期时间：2分钟
     */
    REDIS_DEMO_KEY("redis_demo_key", 2 * 60L),

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
