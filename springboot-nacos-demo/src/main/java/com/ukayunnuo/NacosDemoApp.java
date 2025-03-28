package com.ukayunnuo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@EnableScheduling
@SpringBootApplication
public class NacosDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(NacosDemoApp.class, args);
    }
}
