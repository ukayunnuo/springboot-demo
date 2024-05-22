package com.ukayunnuo.init;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.core.ErrorCode;
import com.ukayunnuo.core.RedisKey;
import com.ukayunnuo.core.exception.ServiceException;
import com.ukayunnuo.function.GetNodeFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.commands.JedisClusterCommands;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * init Cluster Nodes And Slot
 *
 * @author yunnuo
 * @date 2024-05-22
 */
@Slf4j
@Component
public class ClusterNodesAndSlotInitHandle {

    /**
     * node 与 slot 区间映射 mapping
     */
    public static Map<String, String> NODE_SLOT_MAPPING;

    /**
     * node 与 hashtag key 映射 mapping
     */
    public static Map<String, String> NODE_HASHTAG_KEY_MAPPING;

    @Resource
    private JedisCluster jedisCluster;

    @Resource
    private JedisClusterCommands jedisClusterCommands;

    private final GetNodeFunction getNodeFunction = new GetNodeFunction();

    @PostConstruct
    void init() {
        long start = System.currentTimeMillis();
        log.info("ClusterNodesAndSlotInitHandle init start!");
        Boolean flag = Boolean.TRUE;
        try {

            // 获取当前集群节点与 slot 区间映射
            Map<String, String> nodeSlotMapping = initNodeSlotMapping();

            // 获取之前存储在redis中的 集群节点与 pool key 映射
            Map<String, String> nodeSlotMappingCacheMap = jedisClusterCommands.hgetAll(RedisKey.NODE_SLOT_MAPPING.getPrefix());
            Map<String, String> HashTagKeyMapMappingCacheMap = jedisClusterCommands.hgetAll(RedisKey.NODE_HASHTAG_KEY_MAPPING.getPrefix());

            if (MapUtil.isEmpty(nodeSlotMappingCacheMap)) {
                Map<String, String> hashTagKeyMapMapping = initHashTagKeyMapMapping(nodeSlotMapping);
                setInitCache(nodeSlotMapping, hashTagKeyMapMapping);
                return;
            }

            // 判断集群节点是否有更新
            if (Objects.equals(nodeSlotMappingCacheMap, nodeSlotMapping)) {
                NODE_SLOT_MAPPING = nodeSlotMappingCacheMap;
                NODE_HASHTAG_KEY_MAPPING = HashTagKeyMapMappingCacheMap;
                // 更新缓存过期时间
                jedisClusterCommands.expire(RedisKey.NODE_SLOT_MAPPING.getPrefix(), RedisKey.NODE_SLOT_MAPPING.expireTime());
                jedisClusterCommands.expire(RedisKey.NODE_HASHTAG_KEY_MAPPING.getPrefix(), RedisKey.NODE_HASHTAG_KEY_MAPPING.expireTime());
            } else {
                Map<String, String> hashTagKeyMapMapping = initHashTagKeyMapMapping(nodeSlotMapping);
                setInitCache(nodeSlotMapping, hashTagKeyMapMapping);
                // todo: 通过发送消息进行异步处理，将老数据迁移到新的节点中，然后删除老数据，此处不再处理
            }
        } catch (Exception e) {
            flag = Boolean.FALSE;
            log.error("ClusterNodesAndSlotInitHandle init error! e:{}", e.getMessage(), e);
        } finally {
            log.info("ClusterNodesAndSlotInitHandle init end! flag:{}, NODE_SLOT_MAPPING:{}, NODE_POOL_KEY_MAPPING:{} cost：{}ms", flag, JSONObject.toJSONString(NODE_SLOT_MAPPING), JSONObject.toJSONString(NODE_HASHTAG_KEY_MAPPING), System.currentTimeMillis() - start);
        }
    }

    private void setInitCache(Map<String, String> nodeSlotMapping, Map<String, String> hashTagKeyMapMapping) {
        jedisClusterCommands.hmset(RedisKey.NODE_SLOT_MAPPING.getPrefix(), nodeSlotMapping);
        jedisClusterCommands.expire(RedisKey.NODE_SLOT_MAPPING.getPrefix(), RedisKey.NODE_SLOT_MAPPING.expireTime());

        jedisClusterCommands.hmset(RedisKey.NODE_HASHTAG_KEY_MAPPING.getPrefix(), hashTagKeyMapMapping);
        jedisClusterCommands.expire(RedisKey.NODE_HASHTAG_KEY_MAPPING.getPrefix(), RedisKey.NODE_HASHTAG_KEY_MAPPING.expireTime());

        NODE_SLOT_MAPPING = nodeSlotMapping;
        NODE_HASHTAG_KEY_MAPPING = hashTagKeyMapMapping;
    }

    Map<String, String> initNodeSlotMapping() {
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        Map<String, String> nodeSlotMapping = new HashMap<>();
        for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
            Jedis jedisNode = entry.getValue().getResource();
            List<Object> slots = jedisNode.clusterSlots();
            for (Object slotInfo : slots) {
                if (slotInfo == null) {
                    continue;
                }
                if (!(slotInfo instanceof List)){
                    log.error("initNodeSlotMapping 解析slotInfo失败! clusterNodes:{}, slotInfo:{}", JSONObject.toJSONString(clusterNodes), JSONObject.toJSONString(slotInfo));
                    throw new ServiceException(ErrorCode.SYSTEM_ERROR.getCode(), "解析slotInfo失败！");
                }
                List<Object> slot = JSONArray.parseArray(JSONObject.toJSONString(slotInfo), Object.class);
                long startSlot = NumberUtil.parseLong(slot.get(0).toString());
                long endSlot = NumberUtil.parseLong(slot.get(1).toString());

                if (!(slot.get(2) instanceof List)){
                    log.error("initNodeSlotMapping 解析slotInfo[2]失败! clusterNodes:{}, slotInfo:{}", JSONObject.toJSONString(clusterNodes), JSONObject.toJSONString(slotInfo));
                    throw new ServiceException(ErrorCode.SYSTEM_ERROR.getCode(), "解析slotInfo失败！");
                }
                List<String> nodeInfo = JSONArray.parseArray(JSONObject.toJSONString(slot.get(2)), String.class);
                String nodeIp = nodeInfo.get(0);
                long nodePort = NumberUtil.parseLong(nodeInfo.get(1));
                // key格式：nodeIp:nodePort, value 格式：startSlot,endSlot
                nodeSlotMapping.put(String.format("%s:%s", nodeIp, nodePort), String.format("%s,%s", startSlot, endSlot));
            }
        }
        log.info("ClusterNodesAndSlotInitHandle init nodeSlotMapping:{}", JSONObject.toJSONString(nodeSlotMapping));
        return nodeSlotMapping;
    }

    private Map<String, String> initHashTagKeyMapMapping(Map<String, String> nodeSlotMapping) {
        Map<String, String> hashTagKeyMap = randomNodeHashTagKey(nodeSlotMapping);
        log.info("ClusterNodesAndSlotInitHandle init hashTagKeyMap:{}, nodeSlotMapping:{}", JSONObject.toJSONString(hashTagKeyMap), JSONObject.toJSONString(hashTagKeyMap));
        NODE_HASHTAG_KEY_MAPPING = hashTagKeyMap;
        return hashTagKeyMap;
    }

    /**
     * 随机生成 分布在不同node 的 hashtag key
     *
     * @param nodeSlotMapping node slot mapping
     * @return node 与 hashtag key 映射
     */
    private Map<String, String> randomNodeHashTagKey(Map<String, String> nodeSlotMapping) {
        Map<String, String> nodeHashTagKey = new HashMap<>(nodeSlotMapping.size());
        for (int i = 1; i < 10000; i++) {
            String key = String.format("{%s}", i + "_" + RandomUtil.randomString(4));
            String node = getNodeFunction.apply(key, nodeSlotMapping);
            if (node == null) {
                continue;
            }
            if (!nodeHashTagKey.containsKey(node)) {
                nodeHashTagKey.put(node, key);
            }
            if (nodeHashTagKey.size() == nodeSlotMapping.size()) {
                break;
            }
        }
        if (nodeHashTagKey.size() != nodeSlotMapping.size()) {
            log.error("randomNodeHashTagKey failed 请调整随机函数! nodeSlotMapping:{}", JSONObject.toJSONString(nodeSlotMapping));
            throw new ServiceException(ErrorCode.SYSTEM_ERROR.getCode(), "nodeHashTagKey.size() != nodeSlotMapping.size()! 请调整随机函数!");
        }
        return nodeHashTagKey;
    }

}
