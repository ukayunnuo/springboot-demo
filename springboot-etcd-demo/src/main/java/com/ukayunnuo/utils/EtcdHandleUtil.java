package com.ukayunnuo.utils;

import com.ukayunnuo.enums.WatchKeyStatus;
import com.ukayunnuo.watcher.EtcdKeyWatcher;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.kv.PutResponse;
import io.netty.util.internal.StringUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * etcd 处理工具类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-26
 */
@Component
public class EtcdHandleUtil {
    @Resource
    private Client etcdClient;

    @Resource
    private EtcdKeyWatcher etcdKeyWatcher;


    private final Cache watchedKeysCache;

    public static final String CACHE_ETCD_KEYS_FILED = "etcdKeys";


    public EtcdHandleUtil(CacheManager cacheManager) {
        this.watchedKeysCache = cacheManager.getCache(CACHE_ETCD_KEYS_FILED);
    }

    /**
     * 监听并缓存
     *
     * @param key key
     * @return 监听结果
     */
    public WatchKeyStatus watchKeyHandlerAndCache(String key) {
        return etcdKeyWatcher.watchKeyHandlerAndCache(key);
    }

    /**
     * put Key
     *
     * @param key   key
     * @param value 值
     * @return 结果
     */
    public CompletableFuture<PutResponse> put(String key, String value) {
        return etcdClient.getKVClient().put(ByteSequence.from(key, StandardCharsets.UTF_8), ByteSequence.from(value, StandardCharsets.UTF_8));
    }

    /**
     * 获取值
     *
     * @param key key
     * @return 结果
     */
    public String get(String key) {
        Optional<Cache.ValueWrapper> valueWrapper = Optional.ofNullable(watchedKeysCache.get(key));
        if (valueWrapper.isPresent()) {
            return Objects.requireNonNull(valueWrapper.get().get()).toString();
        }
        return StringUtil.EMPTY_STRING;
    }

    /**
     * 获取值
     *
     * @param key key
     * @return 结果
     */
    @Nullable
    public <T> T get(String key, @Nullable Class<T> type) {
        return watchedKeysCache.get(key, type);
    }

    /**
     * 获取值
     *
     * @param key key
     * @return 结果
     */
    @Nullable
    public <T> T get(String key, Callable<T> valueLoader) {
        return watchedKeysCache.get(key, valueLoader);
    }


}
