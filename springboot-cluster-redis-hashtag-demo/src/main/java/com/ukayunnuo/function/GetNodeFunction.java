package com.ukayunnuo.function;

import redis.clients.jedis.util.JedisClusterCRC16;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 获取node节点
 *
 * @author yunnuo
 * @date 2024-05-22
 */
public class GetNodeFunction implements BiFunction<String, Map<String, String>, String> {

    @Override
    public String apply(String key, Map<String, String> nodeSlotMapping) {
        // 计算slot 值
        int slot = JedisClusterCRC16.getSlot(key);
        for (Map.Entry<String, String> entry : nodeSlotMapping.entrySet()) {
            int[] array = Arrays.stream(entry.getValue().split(",")).mapToInt(Integer::parseInt).toArray();
            if (slot >= array[0] && slot <= array[1]) {
                return entry.getKey();
            }
        }
        return null;
    }
}
