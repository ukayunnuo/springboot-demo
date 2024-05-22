package com.ukayunnuo.enums.poolsharding;

/**
 * POOL_1 分片枚举
 *
 * @author yunnuo
 * @date 2024-05-22
 */
public enum Pool1Sharding {

    POOL_1_A(PoolShardingType.A, 8000, 10000, 400000),

    POOL_1_B(PoolShardingType.B, 5000, 7999, 300000),

    POOL_1_C(PoolShardingType.C, 0, 4999, 200000),

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

    Pool1Sharding(PoolShardingType type, Integer limitMinScore, Integer limitMaxScore, Integer saveSizeLimit) {
        this.type = type;
        this.limitMinScore = limitMinScore;
        this.limitMaxScore = limitMaxScore;
        this.saveSizeLimit = saveSizeLimit;
    }

    public static PoolShardingType getSharding(Integer score) {
        for (Pool1Sharding poolSharding : Pool1Sharding.values()) {
            if (score >= poolSharding.limitMinScore && score <= poolSharding.limitMaxScore) {
                return poolSharding.type;
            }
        }
        return null;
    }

}
