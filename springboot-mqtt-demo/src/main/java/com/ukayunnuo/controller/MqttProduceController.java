package com.ukayunnuo.controller;

import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.core.MqttMsgStruct;
import com.ukayunnuo.enums.MqttQos;
import com.ukayunnuo.utils.MqttProducerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@RestController
@RequestMapping("/demo/mqtt/produce")
public class MqttProduceController {

    @Autowired(required = false)
    private MqttProducerUtil mqttProducerUtil;

    @PostMapping("/sendMsg")
    public Result<Boolean> deleteExchange(@RequestBody MqttMsgStruct msgStruct) {
        mqttProducerUtil.send(msgStruct.getTopic(), Objects.nonNull(msgStruct.getQos()) ? msgStruct.getQos().code : MqttQos.QOS_0.code, msgStruct.getMessage());
        return Result.success(Boolean.TRUE);
    }
}