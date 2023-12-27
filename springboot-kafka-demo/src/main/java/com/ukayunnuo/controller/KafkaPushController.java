package com.ukayunnuo.controller;

import com.ukayunnuo.core.Result;
import com.ukayunnuo.core.exception.ServiceException;
import com.ukayunnuo.domain.request.KafkaPushDemoReq;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * kafka 测试 api 接口
 *
 * @author yunnuo
 * @since 1.0.0
 */
@RestController
@RequestMapping("/demo/kafka")
public class KafkaPushController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * kafka 发送消息 (无结果)
     *
     * @param req 请求参数
     * @return 结果
     */
    @PostMapping("pushTest")
    public Result<Boolean> pushMsgTest(@RequestBody KafkaPushDemoReq req) {
        kafkaTemplate.send(req.getTopic(), req.getMsg());
        return Result.success(Boolean.TRUE);
    }

    /**
     * kafka 发送消息 (有结果)
     *
     * @param req 请求参数
     * @return 结果
     */
    @PostMapping("pushCallbackTest")
    public Result<SendResult<String, String>> pushCallbackMsgTest(@RequestBody KafkaPushDemoReq req) {

        try {
            SendResult<String, String> res = kafkaTemplate.send(req.getTopic(), req.getMsg()).get();
            return Result.success(res);
        } catch (InterruptedException | ExecutionException e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

}
