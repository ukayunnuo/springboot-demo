package com.ukayunnuo.watcher;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.config.EtcdProperties;
import com.ukayunnuo.enums.WatchKeyStatus;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * etcd 监听器
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-25
 */
@Slf4j
@Component
public class EtcdKeyWatcher {

    @Resource
    private Client etcdClient;

    @Resource
    private EtcdProperties etcdProperties;

    private final Cache watchedKeysCache;

    public static final String CACHE_ETCD_KEYS_FILED = "etcdKeys";


    public EtcdKeyWatcher(CacheManager cacheManager) {
        this.watchedKeysCache = cacheManager.getCache(CACHE_ETCD_KEYS_FILED);
    }

    public WatchKeyStatus watchKeyHandlerAndCache(String key) {

        if (Objects.nonNull(watchedKeysCache.get(key))) {
            return WatchKeyStatus.NO_NEED_MONITOR;
        }

        if (StrUtil.isBlank(etcdProperties.getWatchKeyPrefix())) {
            return WatchKeyStatus.NO_MONITOR;
        }

        boolean keyPrefixFlag = Arrays.stream(etcdProperties.getWatchKeyPrefix().split(","))
                .filter(StrUtil::isNotBlank)
                .map(String::trim).anyMatch(prefix -> key.substring(0, key.indexOf(".")).equals(prefix));
        if (Boolean.FALSE.equals(keyPrefixFlag)) {
            String value = getValueForKVClient(key);
            if (StrUtil.isNotBlank(value)) {
                // 直接缓存, 不进行监听
                watchedKeysCache.put(key, value);
                return WatchKeyStatus.CACHE_NO_MONITOR;
            }
            return WatchKeyStatus.FAILED;
        }

        WatchOption watchOption = WatchOption.builder().withRange(ByteSequence.from(key, StandardCharsets.UTF_8)).build();

        Watch.Listener listener = Watch.listener(res -> {
            for (WatchEvent event : res.getEvents()) {
                log.info("Watch.listener event:{}", JSONObject.toJSONString(event));
                KeyValue keyValue = event.getKeyValue();
                if (Objects.nonNull(keyValue)) {
                    // 将监听的键缓存到本地缓存中
                    watchedKeysCache.put(keyValue.getKey().toString(StandardCharsets.UTF_8), keyValue.getValue().toString(StandardCharsets.UTF_8));
                    log.info("watchClient.watch succeed! key:{}", key);
                }
            }
        });

        Watch watchClient = etcdClient.getWatchClient();

        watchClient.watch(ByteSequence.from(key,StandardCharsets.UTF_8), watchOption, listener);

        return WatchKeyStatus.SUCCEEDED;
    }

    public String getValueForKVClient(String key) {
        KV kvClient = etcdClient.getKVClient();
        ByteSequence keyByteSequence = ByteSequence.from(key, StandardCharsets.UTF_8);

        GetResponse response;
        try {
            response = kvClient.get(keyByteSequence).get();
        } catch (Exception e) {
            // 处理异常情况
            log.error("etcdClient.getKVClient error! key:{}, e:{}", key, e.getMessage(), e);
            return null;
        }

        if (response.getKvs().isEmpty()) {
            return null;
        }

        return response.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8);
    }

}
