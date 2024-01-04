package com.ukayunnuo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域 配置类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-03
 */
@Configuration
public class CoreConfiguration implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 所有接口
        registry.addMapping("/**")
                // 是否发送Cookie
                .allowCredentials(true)
                // 支持域
                .allowedOriginPatterns("*")
                // 支持方法
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
                // 请求头
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

}
