package com.ukayunnuo.consumer;

import com.rabbitmq.client.Channel;
import com.ukayunnuo.constants.RabbitConstants;
import com.ukayunnuo.core.MqMsgStruct;
import com.ukayunnuo.handle.ChannelHandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Direct 路由模式 消费者
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-18
 */
@Slf4j
@Component
public class DirectRoutingModeDemoConsumer {

    @RabbitListener(queues = {RabbitConstants.Queue.ROUTING_MODE_QUEUE_DEMO_1})
    public void handle1(MqMsgStruct msg, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("DirectRoutingModeDemoConsumer handle1 start --> queue：{}, msg:{}, deliveryTag:{}", message.getMessageProperties().getConsumerQueue(), msg, deliveryTag);
        ChannelHandlerUtils.basicAckAndRecover(msg, message, channel, deliveryTag);
    }

    @RabbitListener(queues = {RabbitConstants.Queue.ROUTING_MODE_QUEUE_DEMO_2})
    public void handle2(MqMsgStruct msg, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("DirectRoutingModeDemoConsumer handle2 start --> queue：{}, msg:{}, deliveryTag:{}", message.getMessageProperties().getConsumerQueue(), msg, deliveryTag);
        ChannelHandlerUtils.basicAckAndRecover(msg, message, channel, deliveryTag);
    }

}
