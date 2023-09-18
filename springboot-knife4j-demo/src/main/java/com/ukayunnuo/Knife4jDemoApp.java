package com.ukayunnuo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 启动类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@SpringBootApplication
public class Knife4jDemoApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Knife4jDemoApp.class, args);
        Environment environment = context.getBean(Environment.class);

        try {
            log.info("\n😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉😉\n\n\t" +
                    environment.getProperty("spring.application.name") + " Startup success ! Access URLs:\n\t" +
                    "Internal interface access：http://127.0.0.1:" + environment.getProperty("server.port") + "/doc.html\n\t" +
                    "External interface access：http://" + InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port") + "/doc.html\n" +
                    "\n😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍😍\n");
        } catch (UnknownHostException e) {
            log.error("Knife4jDemoApp SERVICE Unknown error：{}", e.getMessage(), e);

        }
    }
}
