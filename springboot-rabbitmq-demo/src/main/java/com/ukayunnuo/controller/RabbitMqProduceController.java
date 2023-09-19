package com.ukayunnuo.controller;

import com.ukayunnuo.core.MqMsgStruct;
import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.request.MqReq;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * RabbitMq produce测试 api 接口
 *
 * @author yunnuo
 * @since 1.0.0
 */
@RestController
@RequestMapping("/demo/rabbitmq/produce")
public class RabbitMqProduceController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/sendMq")
    public Result<Boolean> sendMq(@RequestBody MqReq req) {
        rabbitTemplate.convertAndSend(req.getQueue(), MqMsgStruct.builder().msg(req.getMsg()).build());
        return Result.success(Boolean.TRUE);
    }

}
