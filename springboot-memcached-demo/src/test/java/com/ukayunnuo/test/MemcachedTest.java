package com.ukayunnuo.test;

import com.ukayunnuo.MemcachedApp;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeoutException;

/**
 * Memcached 测试类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-09-25
 */
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
