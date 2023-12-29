package com.ukayunnuo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * websocket 配置类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-12-29
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig {

    /**
     * 注入该bean 自动注册使用 @ServerEndpoint注解声明的Websocket endpoint
     *
     * @return {@link ServerEndpointExporter} bean
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


}
