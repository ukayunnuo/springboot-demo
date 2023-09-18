package com.ukayunnuo.config;

import com.ukayunnuo.constants.RabbitConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMq 配置
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-18
 */
@Slf4j
@Configuration
public class RabbitMqConfig {

    /**
     * 队列声明：直连模式测试
     */
    @Bean
    public Queue directDemoQueue() {
        return new Queue(RabbitConstants.QueueConstants.DIRECT_MODE_QUEUE_DEMO);
    }


    /**
     * Exchange Fanout声明：直连模式测试
     */
    @Bean
    public FanoutExchange directDemoExchange() {
        return new FanoutExchange(RabbitConstants.ExchangeConstants.DIRECT_MODE_EXCHANGE_DEMO);
    }


    /**
     * Binding 声明: 直连模式测试
     *
     * @param directDemoQueue    绑定队列
     * @param directDemoExchange 绑定交换机
     * @return {@link Binding}
     */
    @Bean
    public Binding directFanoutDemoBinding(Queue directDemoQueue, FanoutExchange directDemoExchange) {
        return BindingBuilder.bind(directDemoQueue).to(directDemoExchange);
    }


    /**
     * 队列声明：Fanout模式测试
     */
    @Bean
    public Queue fanoutDemoQueue() {
        return new Queue(RabbitConstants.QueueConstants.FANOUT_MODE_QUEUE_DEMO);
    }


    /**
     * Exchange Fanout声明：Fanout模式测试
     */
    @Bean
    public FanoutExchange fanoutDemoExchange() {
        return new FanoutExchange(RabbitConstants.ExchangeConstants.FANOUT_MODE_EXCHANGE_DEMO);
    }


    /**
     * Binding 声明: Fanout模式测试
     *
     * @param fanoutDemoQueue    绑定队列
     * @param fanoutDemoExchange 绑定交换机
     * @return {@link Binding}
     */
    @Bean
    public Binding fanoutDemoBinding(Queue fanoutDemoQueue, FanoutExchange fanoutDemoExchange) {
        return BindingBuilder.bind(fanoutDemoQueue).to(fanoutDemoExchange);
    }


    /**
     * 队列声明：主题模式测试
     */
    @Bean
    public Queue topicDemoQueue() {
        return new Queue(RabbitConstants.QueueConstants.TOPIC_MODE_QUEUE_DEMO);
    }


    /**
     * Exchange 声明： topic模式测试
     */
    @Bean
    public TopicExchange topicDemoExchange() {
        return new TopicExchange(RabbitConstants.ExchangeConstants.TOPIC_MODE_EXCHANGE_DEMO);
    }


    /**
     * Binding 声明: topic模式测试
     *
     * @param topicDemoQueue    绑定队列
     * @param topicDemoExchange 绑定交换机
     * @return {@link Binding}
     */
    @Bean
    public Binding topicDemoBinding(Queue topicDemoQueue, TopicExchange topicDemoExchange) {
        return BindingBuilder.bind(topicDemoQueue).to(topicDemoExchange).with(RabbitConstants.TopicRoutingKey.ROUTING_KEY_DEMO);
    }

    /**
     * 队列声明：延迟队列模式测试
     */
    @Bean
    public Queue delayDemoQueue() {
        return new Queue(RabbitConstants.QueueConstants.DELAY_MODE_QUEUE_DEMO);
    }


    /**
     * Exchange 声明： 延迟队列模式测试
     */
    @Bean
    public CustomExchange delayDemoExchange() {
        Map<String, Object> arguments = new HashMap<>(1);
        arguments.put("x-delayed-type", "direct");

        return new CustomExchange(
                RabbitConstants.ExchangeConstants.DELAY_MODE_EXCHANGE_DEMO,
                "x-delayed-message",
                Boolean.TRUE,
                Boolean.FALSE,
                arguments);
    }


    /**
     * Binding 声明: 延迟队列模式测试
     *
     * @param delayDemoQueue    绑定队列
     * @param delayDemoExchange 绑定交换机
     * @return {@link Binding}
     */
    @Bean
    public Binding topicDemoBinding(Queue delayDemoQueue, CustomExchange delayDemoExchange) {
        return BindingBuilder
                .bind(delayDemoQueue)
                .to(delayDemoExchange)
                .with(RabbitConstants.QueueConstants.DELAY_MODE_QUEUE_DEMO).
                noargs();
    }

}
