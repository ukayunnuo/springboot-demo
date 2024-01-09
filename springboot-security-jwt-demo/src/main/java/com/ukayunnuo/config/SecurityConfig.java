package com.ukayunnuo.config;


import com.ukayunnuo.core.filter.JwtAuthenticationTokenFilter;
import com.ukayunnuo.core.processor.AuthenticationEntryPointImpl;
import com.ukayunnuo.core.processor.LogoutSuccessHandlerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Resource;

/**
 * spring security配置
 * 参考：<a href="https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter/">WebSecurityConfigurerAdapter 遗弃更新文档</a>
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-05
 */
@Slf4j
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    /**
     * 自定义用户认证逻辑
     */
    @Resource
    private UserDetailsService userDetailsService;

    /**
     * 认证失败处理
     */
    @Resource
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 退出处理
     */
    @Resource
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * token认证过滤器
     */
    @Resource
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    @Resource
    private CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        LogoutConfigurer<HttpSecurity> httpSecurityLogoutConfigurer = httpSecurity
                // CSRF禁用
                .csrf().disable()
                // 自定义登录路径
                .formLogin().loginProcessingUrl("/security/jwt/demo/login")
                .usernameParameter("userName")
                .passwordParameter("password")
                .permitAll().and()
                // 认证失败处理
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                // 基于token 认证
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .userDetailsService(userDetailsService)
                // 添加过滤器 jwt
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加CORS filter
                .addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class)
                .addFilterBefore(corsFilter, LogoutFilter.class)
                .logout().logoutUrl("/security/jwt/demo/logout").logoutSuccessHandler(logoutSuccessHandler);

        // 白名单
        httpSecurityLogoutConfigurer.and()
                .authorizeRequests()
                // 匿名
                .antMatchers(
                        "/security/jwt/demo/login",
                        "/security/anonymous1",
                        "/security/anonymous12"
                ).anonymous()
                // 无限制
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/favicon*",
                        "/*.html"
                ).permitAll()
                // 除去白名单都需要进行认证
                .anyRequest().authenticated().and().headers().frameOptions().disable();

        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 忽略请求
        return (web) -> web.ignoring().antMatchers(
                "/ignore1",
                "/ignore2"
        );

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
