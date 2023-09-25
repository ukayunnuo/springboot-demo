package com.ukayunnuo.watcher;

import cn.hutool.core.util.StrUtil;
import com.ukayunnuo.config.EtcdProperties;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
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

    public void watchKeyHandler(String key) {

        if (StrUtil.isBlank(etcdProperties.getWatchKeyPrefix())) {
            return;
        }

        boolean keyPrefixFlag = Arrays.stream(etcdProperties.getWatchKeyPrefix().split(","))
                .filter(StrUtil::isNotBlank)
                .map(String::trim).anyMatch(prefix -> key.substring(0, key.indexOf(".")).equals(prefix));
        if (Boolean.FALSE.equals(keyPrefixFlag)) {
            String value = getValueForKVClient(key);
            if (Objects.nonNull(value)){
                watchedKeysCache.put(key, value);
            }
            return;
        }

        Watch watchClient = etcdClient.getWatchClient();

        watchClient.watch(ByteSequence.from(key, StandardCharsets.UTF_8), res -> {
            List<WatchEvent> events = res.getEvents();
            for (WatchEvent event : events) {
                KeyValue keyValue = event.getKeyValue();
                if (Objects.nonNull(keyValue)) {
                    // 将监听的键缓存到本地缓存中
                    watchedKeysCache.put(keyValue.getKey().toString(StandardCharsets.UTF_8), keyValue.getValue().toString(StandardCharsets.UTF_8));
                }
            }
        });

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
            // 键不存在的处理逻辑
            return "";
        }

        return response.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8);
    }

}
