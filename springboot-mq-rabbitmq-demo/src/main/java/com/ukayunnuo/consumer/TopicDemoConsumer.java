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
 * Topic 主题模式测试
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-20
 */
@Slf4j
@Component
public class TopicDemoConsumer {

    @RabbitListener(queues = RabbitConstants.Queue.TOPIC_MODE_QUEUE_DEMO_1)
    public void handle1(MqMsgStruct msg, Message message, Channel channel) {
        log.info("TopicDemoConsumer handle1 consumerQueue:{}, receivedRoutingKey:{}, receivedExchange:{}, msg:{}", message.getMessageProperties().getConsumerQueue(), message.getMessageProperties().getReceivedRoutingKey(), message.getMessageProperties().getReceivedExchange(), msg);
        ChannelHandlerUtils.basicAckAndRecover(msg, message, channel);

    }

    @RabbitListener(queues = RabbitConstants.Queue.TOPIC_MODE_QUEUE_DEMO_2)
    public void handle2(MqMsgStruct msg, Message message, Channel channel) {
        log.info("TopicDemoConsumer handle2 consumerQueue:{}, receivedRoutingKey:{}, receivedExchange:{}, msg:{}", message.getMessageProperties().getConsumerQueue(), message.getMessageProperties().getReceivedRoutingKey(), message.getMessageProperties().getReceivedExchange(), msg);
        ChannelHandlerUtils.basicAckAndRecover(msg, message, channel);
    }
}
