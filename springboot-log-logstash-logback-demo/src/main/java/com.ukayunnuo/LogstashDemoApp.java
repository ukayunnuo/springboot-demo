package com.ukayunnuo;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@SpringBootApplication
public class LogstashDemoApp {

    public static void main(String[] args) {

        SpringApplication.run(LogstashDemoApp.class, args);

        log.info("日志测试-->json:{}", JSONObject.of("test", "测试内容"));

    }
}
