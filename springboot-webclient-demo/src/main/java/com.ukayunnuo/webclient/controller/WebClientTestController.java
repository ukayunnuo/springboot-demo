package com.ukayunnuo.webclient.controller;

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
            log.error("发送同步请求 error! req:{}, e:{}", req, e.getMessage(), e);
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
                            log.info("发送异步请求 response:{}", response);
                        }, error -> {  // 请求异常错误处理

                            if (error instanceof WebClientRequestException) {  // 请求异常
                                log.error("发送异步请求 error! --> WebClientRequestException req:{}, e:{}", req, error.getMessage(), error);
                            } else if (error instanceof WebClientResponseException) { // 响应异常
                                log.error("发送异步请求 error! --> WebClientResponseException req:{}, e:{}", req, error.getMessage(), error);
                            } else {
                                // 其他异常
                                log.error("发送异步请求 error! ——> OtherException req:{}, e:{}", req, error.getMessage(), error);
                            }
                        }, () -> { // 正常请求完成的处理
                            log.info("发送异步请求 complete! url:{} ", req.getUrl());
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
