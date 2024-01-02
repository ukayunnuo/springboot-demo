package com.ukayunnuo.controller;

import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.request.SendMsgReq;
import com.ukayunnuo.endpoints.WebSocketDemoEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 发送消息测试
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-02
 */
@Slf4j
@RestController
@RequestMapping("/websocket/demo/sendMsg")
public class SendMsgController {

    @Resource
    private WebSocketDemoEndpoint webSocketDemoEndpoint;

    /**
     * 发送消息测试
     *
     * @param req {@link SendMsgReq} 请求dto
     * @return 发送结果
     */
    @PostMapping
    public Result<Boolean> sendMsg(@RequestBody SendMsgReq req) {
        try {
            webSocketDemoEndpoint.sendMessageToUser(req.getUid(), req.getMsg());
            return Result.success(Boolean.TRUE);
        } catch (Exception e) {
            log.error("send msg error! req:{}, e:{}", req, e.getMessage(), e);
            return Result.error(e.getMessage(), Boolean.FALSE);
        }
    }
}
