package com.ukayunnuo.consumer;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.ukayunnuo.constants.RabbitConstants;
import com.ukayunnuo.core.MqMsgStruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 直连模式 消费者
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-18
 */
@Slf4j
@Component
@RabbitListener(queues = {RabbitConstants.QueueConstants.DIRECT_MODE_QUEUE_DEMO})
public class DirectDemoConsumer {


    /**
     * spring.rabbitmq.listener.direct.acknowledge-mode: auto，则可以用这个方式，会自动ack
     */
    // @RabbitHandler
    public void autoHandle(Message message) {
        log.info("queue：{}, 收到消息：{}", RabbitConstants.QueueConstants.DIRECT_MODE_QUEUE_DEMO, JSONUtil.toJsonStr(message));
        // 进行业务处理
    }

    @RabbitHandler
    public void handle(MqMsgStruct msg, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("start --> queue：{}, msg:{}, deliveryTag:{}", RabbitConstants.QueueConstants.DIRECT_MODE_QUEUE_DEMO, msg, deliveryTag);
        try {
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("channel.basicAck error ! message:{}, msg:{}, channel:{}, e:{}", JSONObject.toJSONString(message), msg, JSONObject.toJSONString(channel), e.getMessage(), e);
            try {
                // 重新压入MQ
                channel.basicRecover();
            } catch (IOException ex) {
                log.error("channel.basicRecover error ! message:{}, msg:{}, channel:{}, e:{}", JSONObject.toJSONString(message), msg, JSONObject.toJSONString(channel), e.getMessage(), e);
            }
        }

    }

}
