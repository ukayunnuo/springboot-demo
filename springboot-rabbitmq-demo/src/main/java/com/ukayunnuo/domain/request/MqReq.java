package com.ukayunnuo.domain.request;

import lombok.Data;

/**
 * mq 测试
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-18
 */
@Data
public class MqReq {

    private String exchange;

    private String routingKey;

    private String queue;

    private String msg;

    /**
     * 设置消息的延迟时间需要通过消息的 headers 属性传递一个键值对，键为 "x-delay"，值为延迟时间的毫秒数
     */
    private Long xDelay;
}
