package com.ukayunnuo.webclient.config;

import com.ukayunnuo.webclient.core.exception.ServiceException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient Config 配置类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@EnableConfigurationProperties(WebClientProperties.class)
@Configuration
public class WebClientConfig {


    @Resource
    private WebClientProperties webClientProperties;

    private static final String TEST_SERVER_BASE_USL_NAME = "test-server";

    /**
     * 默认的webClient
     *
     * @param webClientBuilder webClientBuilder
     * @return WebClient
     */
    @Bean
    public WebClient commonWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }


    /**
     * 自定义测试服务器的webClient
     *
     * @param webClientBuilder webClientBuilder
     * @return WebClient
     */
    @Bean
    public WebClient testServerWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                // 设置基础url
                .baseUrl(webClientProperties.getServerBaseUrls().get(TEST_SERVER_BASE_USL_NAME))
                // 默认请求头 json格式
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .build();
    }


    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientProperties.getConnectTimeout())
                .responseTimeout(Duration.ofMillis(webClientProperties.getResponseTimeout()))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(webClientProperties.getResponseTimeout(), TimeUnit.MILLISECONDS)));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequestAndResponse());
    }


    /**
     * 请求响应日志打印
     *
     * @return ExchangeFilterFunction
     */
    private ExchangeFilterFunction logRequestAndResponse() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            StringBuilder builderLog = new StringBuilder();
            builderLog.append("==> Request 【").append(clientRequest.method()).append("】 url: ").append(clientRequest.url());
            log.info("WebClient Request start! {}", builderLog);
            return Mono.just(clientRequest);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            HttpStatus responseStatusCode = clientResponse.statusCode();
            // 响应错误处理
            if (responseStatusCode.isError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                                    log.warn("WebClient Request failed! error Body:{}", errorBody);
                                    return Mono.error(new ServiceException(responseStatusCode.value(), "Request failed with status code: " + responseStatusCode + ", Error Body: " + errorBody));
                                }
                        );
            }
            return Mono.just(clientResponse);
        }));
    }

}
