package com.ukayunnuo.handle;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.ukayunnuo.core.MqMsgStruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.io.IOException;

/**
 * Channel 处理器
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-20
 */
@Slf4j
public class ChannelHandlerUtils {


    /**
     * 提交和回滚
     *
     * @param msg     消息内容
     * @param message 消息配置和消息体
     * @param channel Channel
     */
    public static void basicAckAndRecover(MqMsgStruct msg, Message message, Channel channel) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("ChannelHandlerUtils channel.basicAck error ! MessageProperties:{}, msg:{}, channel:{}, e:{}", JSONObject.toJSONString(message.getMessageProperties()), msg, JSONObject.toJSONString(channel), e.getMessage(), e);
            try {
                // 重新压入MQ
                channel.basicRecover();
            } catch (IOException ex) {
                log.error("ChannelHandlerUtils channel.basicRecover error ! MessageProperties:{}, msg:{}, channel:{}, e:{}", JSONObject.toJSONString(message.getMessageProperties()), msg, JSONObject.toJSONString(channel), e.getMessage(), e);
            }
        }
    }


    /**
     * 提交和回滚
     *
     * @param msg         消息内容
     * @param message     消息配置和消息体
     * @param channel     Channel
     * @param deliveryTag deliveryTag
     */
    public static void basicAckAndRecover(MqMsgStruct msg, Message message, Channel channel, long deliveryTag) {
        try {
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("ChannelHandlerUtils channel.basicAck error ! MessageProperties:{}, msg:{}, channel:{}, e:{}", JSONObject.toJSONString(message.getMessageProperties()), msg, JSONObject.toJSONString(channel), e.getMessage(), e);
            try {
                // 重新压入MQ
                channel.basicRecover();
            } catch (IOException ex) {
                log.error("ChannelHandlerUtils channel.basicRecover error ! MessageProperties:{}, msg:{}, channel:{}, e:{}", JSONObject.toJSONString(message.getMessageProperties()), msg, JSONObject.toJSONString(channel), e.getMessage(), e);
            }
        }
    }

}
