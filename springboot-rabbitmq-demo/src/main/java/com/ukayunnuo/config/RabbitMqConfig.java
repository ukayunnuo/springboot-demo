package com.ukayunnuo.config;

import com.ukayunnuo.constants.RabbitConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMq 配置
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-18
 */
@Slf4j
@Configuration
public class RabbitMqConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(Boolean.TRUE);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info("MQ Message sent successfully! correlationData:{}, ack:{}, cause:{}", correlationData, ack, cause));
        rabbitTemplate.setReturnsCallback((message) -> log.warn("MQ Message loss！ message:{}", message));
        return rabbitTemplate;
    }


    /**
     * 队列声明：路由模式测试1
     */
    @Bean
    public Queue directRoutingDemoQueue1() {
        return new Queue(RabbitConstants.Queue.ROUTING_MODE_QUEUE_DEMO_1);
    }

    /**
     * 队列声明：路由模式测试2
     */
    @Bean
    public Queue directRoutingDemoQueue2() {
        return new Queue(RabbitConstants.Queue.ROUTING_MODE_QUEUE_DEMO_2);
    }

    /**
     * Direct Exchange声明：直连路由模式测试
     */
    @Bean
    public DirectExchange directDemoExchange() {
        return new DirectExchange(RabbitConstants.Exchange.DIRECT_MODE_EXCHANGE_DEMO);
    }


    /**
     * Binding 声明: 直连路由模式测试1
     *
     * @param directRoutingDemoQueue1 绑定队列1
     * @param directDemoExchange      Direct交换机
     * @return {@link Binding}
     */
    @Bean
    public Binding directFanoutDemoBinding1(Queue directRoutingDemoQueue1, DirectExchange directDemoExchange) {
        return BindingBuilder.bind(directRoutingDemoQueue1).to(directDemoExchange).with(RabbitConstants.RoutingKey.DIRECT_ROUTING_KEY_DEMO);
    }

    /**
     * Binding 声明: 直连路由模式测试2
     *
     * @param directRoutingDemoQueue2 绑定队列2
     * @param directDemoExchange      Direct交换机
     * @return {@link Binding}
     */
    @Bean
    public Binding directFanoutDemoBinding2(Queue directRoutingDemoQueue2, DirectExchange directDemoExchange) {
        return BindingBuilder.bind(directRoutingDemoQueue2).to(directDemoExchange).with(RabbitConstants.RoutingKey.DIRECT_ROUTING_KEY_DEMO);
    }


    /**
     * 队列声明：Fanout模式测试1
     */
    @Bean
    public Queue fanoutDemoQueue1() {
        return new Queue(RabbitConstants.Queue.FANOUT_MODE_QUEUE_DEMO_1);
    }

    /**
     * 队列声明：Fanout模式测试2
     */
    @Bean
    public Queue fanoutDemoQueue2() {
        return new Queue(RabbitConstants.Queue.FANOUT_MODE_QUEUE_DEMO_2);
    }


    /**
     * Exchange Fanout声明：Fanout模式测试
     */
    @Bean
    public FanoutExchange fanoutDemoExchange() {
        return new FanoutExchange(RabbitConstants.Exchange.FANOUT_MODE_EXCHANGE_DEMO);
    }


    /**
     * Binding 声明: Fanout模式测试1
     *
     * @param fanoutDemoQueue1   绑定队列1
     * @param fanoutDemoExchange Fanout交换机
     * @return {@link Binding}
     */
    @Bean
    public Binding fanoutDemoBinding1(Queue fanoutDemoQueue1, FanoutExchange fanoutDemoExchange) {
        return BindingBuilder.bind(fanoutDemoQueue1).to(fanoutDemoExchange);
    }

    /**
     * Binding 声明: Fanout模式测试2
     *
     * @param fanoutDemoQueue2   绑定队列2
     * @param fanoutDemoExchange Fanout交换机
     * @return {@link Binding}
     */
    @Bean
    public Binding fanoutDemoBinding2(Queue fanoutDemoQueue2, FanoutExchange fanoutDemoExchange) {
        return BindingBuilder.bind(fanoutDemoQueue2).to(fanoutDemoExchange);
    }

    /**
     * 队列声明：主题模式测试1
     */
    @Bean
    public Queue topicDemoQueue1() {
        return new Queue(RabbitConstants.Queue.TOPIC_MODE_QUEUE_DEMO_1);
    }

    /**
     * 队列声明：主题模式测试2
     */
    @Bean
    public Queue topicDemoQueue2() {
        return new Queue(RabbitConstants.Queue.TOPIC_MODE_QUEUE_DEMO_2);
    }

    /**
     * Exchange 声明： topic模式测试
     */
    @Bean
    public TopicExchange topicDemoExchange() {
        return new TopicExchange(RabbitConstants.Exchange.TOPIC_MODE_EXCHANGE_DEMO);
    }


    /**
     * Binding 声明: topic模式测试1
     *
     * @param topicDemoQueue1   绑定队列1
     * @param topicDemoExchange topic交换机
     * @return {@link Binding}
     */
    @Bean
    public Binding topicDemoBinding1(Queue topicDemoQueue1, TopicExchange topicDemoExchange) {
        return BindingBuilder.bind(topicDemoQueue1).to(topicDemoExchange).with(RabbitConstants.RoutingKey.TOPIC_ROUTING_KEY_DEMO_SINGLE);
    }

    /**
     * Binding 声明: topic模式测试2
     *
     * @param topicDemoQueue2   绑定队列2
     * @param topicDemoExchange topic交换机
     * @return {@link Binding}
     */
    @Bean
    public Binding topicDemoBinding2(Queue topicDemoQueue2, TopicExchange topicDemoExchange) {
        return BindingBuilder.bind(topicDemoQueue2).to(topicDemoExchange).with(RabbitConstants.RoutingKey.TOPIC_ROUTING_KEY_DEMO_ALL);
    }

    //
    // /**
    //  * 队列声明：延迟队列模式测试
    //  */
    // @Bean
    // public Queue delayDemoQueue() {
    //     return new Queue(RabbitConstants.Queue.DELAY_MODE_QUEUE_DEMO);
    // }
    //
    //
    // /**
    //  * Custom Exchange 声明： 延迟队列模式测试
    //  */
    // @Bean
    // public CustomExchange delayDemoExchange() {
    //     Map<String, Object> arguments = new HashMap<>(1);
    //     arguments.put("x-delayed-type", "direct");
    //
    //     return new CustomExchange(
    //             RabbitConstants.Exchange.DELAY_MODE_EXCHANGE_DEMO,
    //             "x-delayed-message",
    //             Boolean.TRUE,
    //             Boolean.FALSE,
    //             arguments);
    // }
    //
    //
    // /**
    //  * Binding 声明: 延迟队列模式测试
    //  *
    //  * @param delayDemoQueue    绑定队列
    //  * @param delayDemoExchange 绑定交换机
    //  * @return {@link Binding}
    //  */
    // @Bean
    // public Binding topicDemoBinding(Queue delayDemoQueue, CustomExchange delayDemoExchange) {
    //     return BindingBuilder
    //             .bind(delayDemoQueue)
    //             .to(delayDemoExchange)
    //             .with(RabbitConstants.RoutingKey.DELAY_ROUTING_KEY_DEMO).
    //             noargs();
    // }

}
