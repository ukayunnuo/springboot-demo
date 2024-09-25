package com.ukayunnuo.controller;

import com.ukayunnuo.core.MqMsgStruct;
import com.ukayunnuo.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-09-13
 */
@Slf4j
@RestController
@RequestMapping("/demo/rocketmq/")
public class MQProducerController {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private static final String TEST_TOPIC = "DEV_TEST_DEMO";

    @PostMapping("sendMsg")
    public Result<Boolean> sendMsg(@RequestBody MqMsgStruct req) {
        rocketMQTemplate.convertAndSend(TEST_TOPIC, req);
        return Result.success(Boolean.TRUE);
    }
}
