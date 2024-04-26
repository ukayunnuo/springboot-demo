package com.ukayunnuo.robot;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 【kafka发送消息测试】 确保发送消息的有序性
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-04-26
 */
@Slf4j
@Component
public class KafkaSendMsgTestRobot {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    private final static String KafkaTopic = "user.event_push";


    // @Scheduled(cron = "0 35 0 * * ? ", zone = "UTC")
    public void execute() {
        // 查询前一天的埋点数据
        log.info("PushEventRobot execute start!");
        // 示例
        long now = System.currentTimeMillis();
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(JSONObject.of("uid", RandomUtil.randomInt(20), "event", "test_push", "time", now + i * 100));
        }
        // 发送kafka
        list.forEach(item-> {
            try {
                // 组装k, 目的：让同一个用户的事件 发送到同一个 kafka partition 上, 确保负载均衡和消息有序性  kafka是通过key的哈希值来决定分区的
                String k = String.format("%s_%s", item.getString("uid"), item.getString("event"));
                ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(KafkaTopic, k, JSONObject.toJSONString(item));
                // 通过get方法获取到kafka发送消息的响应，达到确保消息发送成功和阻塞的效果，保证发送消息的有序性
                send.get();
            } catch (Exception e){
                log.error("send kafka error item:{}, e:{}", item, e.getMessage(), e);
            }
        });
    }


}
