# 该项目介绍springboot集成etcd中间件，实现etcd的更新监听并缓存到指定前缀key的值到本地服务缓存中

> etcd 是一个分布式键值对存储，设计用来可靠而快速的保存关键数据并提供访问。通过分布式锁，leader选举和写屏障(write
> barriers)来实现可靠的分布式协作。etcd集群是为高可用，持久性数据存储和检索而准备。

以下代码实现的主要业务是：通过etcd自带监听功能，动态将监听的key进行缓存到本地缓存，达到实时监听key的变化，并且不需要多次的网络请求。

## Pom依赖

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-web</artifactId>
</dependency>

        <!-- cache 缓存 -->
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-cache</artifactId>
</dependency>

        <!-- jetcd-core -->
<dependency>
<groupId>io.etcd</groupId>
<artifactId>jetcd-core</artifactId>
<version>0.7.6</version>
</dependency>

```

## yaml配置

```yaml

etcd:
  watch-key-prefix: yn-demo
  endpoints:
    - http://127.0.0.1:2379
    - http://127.0.0.1:2380

```

> 参数说明：
> watch-key-prefix： 参数用于限制服务监听的前缀key
> endpoints：etcd的连接url

## 配置类

### EtcdProperties (etcd 属性配置)

```java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * etcd 属性配置
 *
 * @author yunnuo
 * @date 2023-09-25
 */
@Data
@Component
@ConfigurationProperties(prefix = "etcd")
public class EtcdProperties {

    /**
     * etcd url
     */
    private List<URI> endpoints;


    /**
     * 监听key的前缀
     */
    private String watchKeyPrefix;

}

```

### EtcdConfig(配置类)

```java

import io.etcd.jetcd.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * etcd 配置类
 *
 * @author yunnuo
 * @date 2023-09-25
 */
@Configuration
public class EtcdConfig {
    @Resource
    private EtcdProperties etcdProperties;

    @Bean
    public Client etcdClient() {
        return Client.builder()
                .endpoints(etcdProperties.getEndpoints())
                .build();
    }

}

```

## etcd 实现监听功能（核心）

### 监听器

```java
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

    /**
     * 监听并存储到缓存
     *
     * @param key 配置key
     * @return 监听结果
     */
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

        watchClient.watch(ByteSequence.from(key, StandardCharsets.UTF_8), watchOption, listener);

        return WatchKeyStatus.SUCCEEDED;
    }

    /**
     * 获取 etcd中的 key值
     * @param key 配置key
     * @return 结果
     */
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


```

### 监听枚举类

```java
/**
 * 监听key 状态枚举
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-26
 */
public enum WatchKeyStatus {

    /**
     * 监听成功
     */
    SUCCEEDED,

    /**
     * 监听失败
     */
    FAILED,

    /**
     * 无需再次监听
     */
    NO_NEED_MONITOR,

    /**
     * 不监听
     */
    NO_MONITOR,

    /**
     * 走缓存，但是没有进行监听
     */
    CACHE_NO_MONITOR,
    ;
}

```

## etcd 工具类

```java
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

```

## 进行测试

### 请求dto

```java

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * ETCD test req
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-26
 */
@Data
public class EtcdReq {

    private String key;

    private String value;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

```

### controller API接口测试

```java
/**
 * 测试类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-26
 */
@Slf4j
@RequestMapping("/etcd/demo")
@RestController
public class EtcdTestController {

    @Resource
    private EtcdHandleUtil etcdHandleUtil;

    @PostMapping("/pushTest")
    public Result<PutResponse> pushTest(@RequestBody EtcdReq req) throws ExecutionException, InterruptedException {
        PutResponse putResponse = etcdHandleUtil.put(req.getKey(), req.getValue()).get();
        WatchKeyStatus watchKeyStatus = etcdHandleUtil.watchKeyHandlerAndCache(req.getKey());
        log.info("pushTest  req:{}, putResponse:{}, watchKeyStatus:{}", req, JSONObject.toJSONString(putResponse), watchKeyStatus);
        return Result.success(putResponse);
    }

    @PostMapping("/get")
    public Result<String> get(@RequestBody EtcdReq req) {
        return Result.success(etcdHandleUtil.get(req.getKey()));
    }

}
```
### HTTP请求示例
```http request
### push key 测试

POST http://localhost:8087/etcd/demo/pushTest
Content-Type: application/json

{
  "key": "yn-demo.test",
  "value": "测试数据"
}

### 获取 key 值
POST http://localhost:8087/etcd/demo/get
Content-Type: application/json

{
  "key": "yn-demo.test"
}

```
