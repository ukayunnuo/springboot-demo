package com.ukayunnuo.enums;

/**
 * 数据池类型枚举
 *
 * @author yunnuo
 * @date 2024-05-22
 */
public enum PoolType {

    // 数据池(pool_1,pool_2,pool_3,pool_4,pool_5)
    POOL_1("pool_1", 8, 10000, 30 * 24 * 60 * 60L),

    POOL_2("pool_2", 8, 20000, 10 * 24 * 60 * 60L),

    POOL_3("pool_3", 8, 10000, 20 * 24 * 60 * 60L),

    POOL_4("pool_4", 6, 100000, 40 * 24 * 60 * 60L),

    POOL_5("pool_5", 6, 5000, 60 * 24 * 60 * 60L),
    ;

    /**
     * 名称
     */
    public final String name;

    /**
     * 获取数量限制
     */
    public final Integer pullNumberLimit;

    /**
     * 池子数据大小限制（== 防止池子过大导致性能问题 ==）
     */
    public final Integer saveSizeLimit;

    /**
     * 池子过期时间
     */
    public final long expire;


    PoolType(String name, Integer pullNumberLimit, Integer saveSizeLimit, long expire) {
        this.name = name;
        this.pullNumberLimit = pullNumberLimit;
        this.saveSizeLimit = saveSizeLimit;
        this.expire = expire;
    }
}
