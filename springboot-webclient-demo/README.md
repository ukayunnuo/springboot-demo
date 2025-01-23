# 该项目介绍springboot集成WebClient 实现服务的请求操作

示例中演示了，如何配置WebClient的请求头，请求参数等相关参数，请求响应精细化处理。

## 为什么使用WebClient 不用RestTemplate
在 Spring Framework 5.0 及更高版本中，RestTemplate 已被弃用，推出较新的 WebClient。官方Spring 鼓励 开发人员迁移到新项目的 WebClient。
WebClient 对比 RestTemplate 优点：
- 非阻塞 I/O ：WebClient 构建在 Reactor 之上，它提供了一种非阻塞、反应式的方法来处理 I/O。这可以在高流量应用程序中实现更好的可扩展性和更高的性能。
- 函数式风格 ：WebClient 使用函数式编程风格，可以使代码更易于阅读和理解。它还提供了流畅的 API，可以更轻松地配置和自定义请求。
- 更好地支持流式传输 ：WebClient 支持请求和响应正文的流式传输，这对于处理大文件或实时数据非常有用。
- 改进的错误处理 ：WebClient 提供比 RestTemplate 更好的错误处理和日志记录，从而更轻松地诊断和解决问题。

**重点：即使升级了spring web 6.0.0版本，也无法在HttpRequestFactory中设置请求超时，这是放弃使用 RestTemplate 的最大因素之一。**

## pom依赖

```xml

<!-- webflux -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

```


## 配置

### yaml 配置
```yaml
# webclient 配置
webclient:
  connect-timeout: 5000  # 连接超时时间，单位为毫秒
  response-timeout: 5000  # 响应超时时间，单位为毫秒
  server-base-urls:
    test-server: http://localhost:8081


```

### 配置类

#### Properties类
```java

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * WebClient 参数类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "webclient")
public class WebClientProperties {

    private Integer connectTimeout;

    private Integer responseTimeout;

    private Map<String, String> serverBaseUrls;

}

```

#### 核心配置类
```java
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

```

## 测试
### 测试代码
```java
import com.ukayunnuo.webclient.core.ErrorCode;
import com.ukayunnuo.webclient.core.Result;
import com.ukayunnuo.webclient.core.exception.ServiceException;
import com.ukayunnuo.webclient.domain.request.WebClientReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * WebClient 测试接口
 * 
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@RequestMapping("/demo/webclient")
@RestController
public class WebClientTestController {

    @Resource
    private WebClient commonWebClient;

    @Resource
    private WebClient testServerWebClient;

    @PostMapping("test")
    public Result<String> test() {
        return Result.success("hello webclient!");
    }


    /**
     * 发送同步请求【POST】请求
     *
     * @return 结果
     */
    @PostMapping("/postSynchronousReq")
    public Result<String> postSynchronousReq(@RequestBody WebClientReq req) {
        String response;
        try {
            response = makeCommonReq(req)
                    // 同步阻塞响应
                    .block();
        } catch (Exception e) {
            log.error("postSynchronousReq error! req:{}, e:{}", req, e.getMessage(), e);
            throw new ServiceException(ErrorCode.WEB_CLIENT_ERROR);
        }
        return Result.success(response);
    }

    /**
     * 异步请求
     *
     * @return 结果
     */
    @PostMapping("/postAsyncReq")
    public Result<String> postAsyncReq(@RequestBody WebClientReq req) {
        makeCommonReq(req)
                // 不做任何处理
//                .subscribe()
                .subscribe(response -> { // 响应结果
                            log.info("postAsyncReq response:{}", response);
                        }, error -> {  // 请求异常错误处理

                            if (error instanceof WebClientRequestException) {  // 请求异常
                                log.error("postAsyncReq error! --> WebClientRequestException req:{}, e:{}", req, error.getMessage(), error);
                            } else if (error instanceof WebClientResponseException) { // 响应异常
                                log.error("postAsyncReq error! --> WebClientResponseException req:{}, e:{}", req, error.getMessage(), error);
                            } else {
                                // 其他异常
                                log.error("postAsyncReq error! ——> OtherException req:{}, e:{}", req, error.getMessage(), error);
                            }
                        }, () -> { // 正常请求完成的处理
                            log.info("postAsyncReq complete! url:{} ", req.getUrl());
                        }
                );
        return Result.success("调用完成！");
    }


    public Mono<String> makeCommonReq(WebClientReq req) {
        return commonWebClient.post()
                .uri(req.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req.getRequestBody().toString())
                .retrieve()
                // 请求响应非200 异常处理
                .onStatus(httpStatus -> httpStatus.value() != 200, clientResponse -> {
                    log.error("WebClient error! req:{}, statusCode:{}, e:{}", req, clientResponse.statusCode(), clientResponse.statusCode().getReasonPhrase());
                    return Mono.error(new ServiceException(ErrorCode.WEB_CLIENT_ERROR));
                })
                .bodyToMono(String.class);
    }

}

```



### 请求http测试

```http request

### （同步请求示例）-- 正常请求
POST http://localhost:8080/demo/webclient/postSynchronousReq
Content-Type: application/json

{
  "url": "http://localhost:8080/demo/webclient/test",
  "requestBody": {}
}

### （异步请求示例） -- 非正常请求
POST http://localhost:8080/demo/webclient/postAsyncReq
Content-Type: application/json

{
  "url": "http://localhost:8080/demo/bug",
  "requestBody": {}
}

```


