package com.ukayunnuo.enums.poolsharding;

/**
 * POOL_4 分片枚举
 *
 * @author yunnuo
 * @date 2024-05-22
 */
public enum Pool4Sharding {

    POOL_4_A(PoolShardingType.A, 80, 100, 10),

    POOL_4_B(PoolShardingType.B, 60, 79, 200),

    POOL_4_C(PoolShardingType.C, 0, 59, 1000),

    ;

    /**
     * 分片类型
     */
    public final PoolShardingType type;

    /**
     * 分片最小值
     */
    public final Integer limitMinScore;

    /**
     * 分片最大值
     */
    public final Integer limitMaxScore;

    /**
     * 保存数量限制
     */
    public final Integer saveSizeLimit;

    Pool4Sharding(PoolShardingType type, Integer limitMinScore, Integer limitMaxScore, Integer saveSizeLimit) {
        this.type = type;
        this.limitMinScore = limitMinScore;
        this.limitMaxScore = limitMaxScore;
        this.saveSizeLimit = saveSizeLimit;
    }

    public static PoolShardingType getSharding(Integer score) {
        for (Pool4Sharding poolSharding : Pool4Sharding.values()) {
            if (score >= poolSharding.limitMinScore && score <= poolSharding.limitMaxScore) {
                return poolSharding.type;
            }
        }
        return null;
    }

}
