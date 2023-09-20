package com.ukayunnuo.controller;

import cn.hutool.core.util.StrUtil;
import com.ukayunnuo.core.MqMsgStruct;
import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.request.MqReq;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
        String routingKey = StrUtil.blankToDefault(req.getRoutingKey(), req.getQueue());
        if (StrUtil.isBlank(req.getExchange())) {
            rabbitTemplate.convertAndSend(routingKey, MqMsgStruct.builder().msg(req.getMsg()).build());
        } else {
            rabbitTemplate.convertAndSend(req.getExchange(), routingKey, MqMsgStruct.builder().msg(req.getMsg()).build());
        }
        return Result.success(Boolean.TRUE);
    }

    @PostMapping("/sendDelayMq")
    public Result<Boolean> sendDelayMq(@RequestBody MqReq req) {
        String routingKey = StrUtil.blankToDefault(req.getRoutingKey(), req.getQueue());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader(MessageProperties.X_DELAY, Objects.nonNull(req.getXDelay()) ? req.getXDelay() : 2000L);
        Message message = MessageBuilder.withBody(MqMsgStruct.builder().msg(req.getMsg()).build().toString().getBytes(StandardCharsets.UTF_8)).andProperties(messageProperties).build();
        if (StrUtil.isBlank(req.getExchange())) {
            rabbitTemplate.convertAndSend(routingKey, message);
        } else {
            rabbitTemplate.send(req.getExchange(), routingKey, message);
        }
        return Result.success(Boolean.TRUE);
    }

}
