package com.ukayunnuo.controller;

import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.request.MqReq;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * RabbitAdmin 测试 API 接口
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-20
 */
@RestController
@RequestMapping("/demo/rabbitmq/admin")
public class RabbitAdminTestController {

    @Resource
    private RabbitAdmin rabbitAdmin;


    @PostMapping("/deleteExchange")
    public Result<Boolean> deleteExchange(@RequestBody MqReq req){
        return Result.success(rabbitAdmin.deleteExchange(req.getExchange()));
    }

    @PostMapping("/deleteQueue")
    public Result<Boolean> deleteQueue(@RequestBody MqReq req){
        return Result.success(rabbitAdmin.deleteQueue(req.getQueue()));
    }
}
