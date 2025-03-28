package com.ukayunnuo.es;

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
public class EsDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(EsDemoApp.class, args);
    }

}
