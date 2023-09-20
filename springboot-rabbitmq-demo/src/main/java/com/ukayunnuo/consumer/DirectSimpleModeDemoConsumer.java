package com.ukayunnuo.consumer;

import com.rabbitmq.client.Channel;
import com.ukayunnuo.constants.RabbitConstants;
import com.ukayunnuo.core.MqMsgStruct;
import com.ukayunnuo.handle.ChannelHandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Direct 简单模式 消费者
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-20
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitConstants.Queue.DIRECT_MODE_QUEUE_DEMO)
public class DirectSimpleModeDemoConsumer {

    @RabbitHandler
    public void handle(MqMsgStruct msg, Message message, Channel channel) {
        log.info("DirectSimpleModeDemoConsumer handle start --> queue：{}, msg:{}", message.getMessageProperties().getConsumerQueue(), msg);
        ChannelHandlerUtils.basicAckAndRecover(msg, message, channel);
    }

}
