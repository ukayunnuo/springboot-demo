package com.ukayunnuo.test;

import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.core.exception.ServiceException;
import com.ukayunnuo.enums.KafkaTopic;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDemoKafkaAppTest {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 测试发送消息 自定义
     */
    @Test
    public void testCustomSendBatch() {
        try {
            SendResult<String, String> result = kafkaTemplate.send(KafkaTopic.TOPIC_TEST_CUSTOM_BATCH_MODEL.topic, "testCustomSendBatch send...").get();
            log.info("testCustomSendBatch result:{}", JSONObject.toJSONString(result));
        } catch (InterruptedException | ExecutionException e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    /**
     * 测试发送消息 single
     */
    @Test
    public void testSendSingle() {
        kafkaTemplate.send(KafkaTopic.TOPIC_TEST_SINGLE_MODEL.topic, "testSendSingle send...");
    }

    /**
     * 测试发送消息 batch
     */
    @Test
    public void testSendBatch() {
        kafkaTemplate.send(KafkaTopic.TOPIC_TEST_BATCH_MODEL.topic, "testSendBatch send...");
    }



}
