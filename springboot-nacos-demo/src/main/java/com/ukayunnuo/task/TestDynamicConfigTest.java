package com.ukayunnuo.task;

import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.config.TestDynamicConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 动态配置更新测试
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Component
public class TestDynamicConfigTest {

    @Resource
    private TestDynamicConfig testDynamicConfig;

    @Scheduled(fixedDelay = 5000)
    public void test() {
        log.info("testDynamicConfig config:{}", JSONObject.toJSONString(testDynamicConfig));
    }

}
