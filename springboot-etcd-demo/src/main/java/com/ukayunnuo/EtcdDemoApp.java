package com.ukayunnuo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 启动类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@EnableCaching
@SpringBootApplication
public class EtcdDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(EtcdDemoApp.class, args);
    }

}
