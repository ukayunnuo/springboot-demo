package com.ukayunnuo;

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
public class Auth0JwtDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(Auth0JwtDemoApp.class, args);
    }

}
