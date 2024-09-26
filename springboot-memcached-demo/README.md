# Springboot 集成 Memcached 框架

## Memcached 介绍

Memcached是一个自由开源的，高性能，分布式内存对象缓存系统。它通过在内存中缓存数据和对象来减少读取数据库的次数，从而提高网站或应用的响应速度。
Memcached是一种基于内存的key-value存储，用来存储小块的任意数据（字符串、对象），这些数据可以是数据库调用、API调用或者是页面渲染的结果。其存储性能在某些方面不比
redis差，甚至在文本类型数据的存储上性能略优于 redis

## Memcached 安装部署

### docker 安装

```docker
docker run -d \
  --name memcached-server \
  -p 11211:11211 \
  -e MEMCACHED_MEM_LIMIT=256 \
  --restart always \
  memcached:1.6.12
```

### docker-compose 安装

```yaml
version: '3'
services:
  memcached:
    image: memcached:1.6.12
    restart: always
    container_name: memcached-server
    ports:
      - "11211:11211"
    environment:
      - MEMCACHED_MEM_LIMIT=256 # 限制256M
```

### Springboot 集成 Memcached

1. 添加POM依赖

```xml

<!--XMemcached -->
<dependency>
    <groupId>com.googlecode.xmemcached</groupId>
    <artifactId>xmemcached</artifactId>
    <version>${xmemcached.version}</version>
</dependency>

```

2. yml 配置Memcached连接
```yaml
memcached:
  # memcached服务器节点
  servers: 127.0.0.1:11211
  # 是否启用
  enable: true
  # nio连接池的数量
  poolSize: 10
  # 设置默认操作超时
  opTimeout: 3000
  # 是否启用url encode机制
  sanitizeKeys: false
```

3. Memcached配置类
```java
@Slf4j
@Configuration
public class MemcachedConfig {

    @Resource
    private MemcachedProperties memcachedProperties;

    @Bean
    public MemcachedClient memcachedClient() throws Exception {
        MemcachedClient memcachedClient = null;
        if (memcachedProperties.getEnable()) {
            try {
                MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(memcachedProperties.getServers()));
                builder.setSanitizeKeys(memcachedProperties.getSanitizeKeys());
                builder.setConnectionPoolSize(memcachedProperties.getPoolSize());
                builder.setOpTimeout(memcachedProperties.getOpTimeout());
                builder.setFailureMode(false);
                builder.setSessionLocator(new KetamaMemcachedSessionLocator());
                builder.setCommandFactory(new BinaryCommandFactory());
                memcachedClient = builder.build();
            } catch (IOException e) {
                log.error("memcachedClient build error! case: {}", e.getMessage(), e);
                throw e;
            }
        }
        return memcachedClient;
    }
}

```
4. 测试类
```java

@Slf4j
@SpringBootTest(classes = MemcachedApp.class)
public class MemcachedTest {

    @Resource
    private MemcachedClient memcachedClient;

    @Test
    public void test() throws InterruptedException, TimeoutException, MemcachedException {
        // 设置缓存，永不过期
        memcachedClient.set("test-key1", 0, "测试1");

        // 设置缓存，10秒过期
        memcachedClient.set("test-key2", 10, "test2");

        // 延缓缓存过期时间20秒
        memcachedClient.touch("test-key2", 20);

        String testKey = memcachedClient.get("test-key1");
        log.info("get key value:{}", testKey);

        // 移除key
        memcachedClient.delete("test-key1");


    }
}

```

