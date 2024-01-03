package com.ukayunnuo.config;

import com.github.pagehelper.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PageHelper 配置类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-03
 */
@Configuration
public class PageHelperConfig {

    /**
     * pagehelper 拦截器
     *
     * @return {@link PageInterceptor}
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

}
