# 该项目介绍了springboot如何集成rabbitMQ消息中间件，实现(直连模式\路由模式\广播模式\主题模式)的消息发送和接收

## pom依赖

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- amqp -->
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

<!-- lombok -->
<dependency>
<groupId>org.projectlombok</groupId>
<artifactId>lombok</artifactId>
<version>${lombok.version}</version>
</dependency>

<!-- fastjson2 -->
<dependency>
<groupId>com.alibaba.fastjson2</groupId>
<artifactId>fastjson2</artifactId>
<version>${fastjson2.version}</version>
</dependency>

<!--    hutool工具类    -->
<dependency>
<groupId>cn.hutool</groupId>
<artifactId>hutool-all</artifactId>
<version>${hutool.version}</version>
</dependency>

```

## 配置

### yaml

```yaml

spring:
  rabbitmq:
    # 连接地址
    host: 127.0.0.1
    # 端口
    port: 5672
    username: ${RABBITMQ_USERNAME:guest}
    password: ${RABBITMQ_PASSWORD:guest}
    virtual-host: /
    # 手动提交消息
    listener:
      simple:
        #        acknowledge-mode: auto
        acknowledge-mode: manual
      direct:
        #        acknowledge-mode: auto
        acknowledge-mode: manual
```

### Java配置类

```java

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

}


```

## 生产者

```java
/**
 * RabbitMq produce测试 api 接口
 *
 * @author yunnuo
 * @since 1.0.0
 */
@RestController
@RequestMapping("/demo/rabbitmq/produce")
public class RabbitMqProduceController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/sendMq")
    public Result<Boolean> sendMq(@RequestBody MqReq req) {
        String routingKey = StrUtil.blankToDefault(req.getRoutingKey(), req.getQueue());
        if (StrUtil.isBlank(req.getExchange())) {
            rabbitTemplate.convertAndSend(routingKey, MqMsgStruct.builder().msg(req.getMsg()).build());
        } else {
            rabbitTemplate.convertAndSend(req.getExchange(), routingKey, MqMsgStruct.builder().msg(req.getMsg()).build());
        }
        return Result.success(Boolean.TRUE);
    }

}

```

#### HTTP测试

```http request

### 发送mq消息 direct 简单模式 -> 端对端
POST http://localhost:8084/demo/rabbitmq/produce/sendMq
Content-Type: application/json

{
  "queue": "direct.queue.demo",
  "msg": "direct simple test msg info"
}

### 发送mq消息 direct路由模式 -> 指定routingKey 示例:{direct.routing.demo}, 注意：如果传的是routingKey, 必须传exchange
POST http://localhost:8084/demo/rabbitmq/produce/sendMq
Content-Type: application/json

{
  "exchange": "direct.exchange.demo",
  "routingKey": "direct.routing.demo",
  "msg": "direct routing test msg info"
}

### 发送mq消息 direct路由模式 -> 指定队列 示例:{routing.queue.demo1, routing.queue.demo2}
POST http://localhost:8084/demo/rabbitmq/produce/sendMq
Content-Type: application/json

{
  "exchange": "direct.exchange.demo",
  "routingKey": "routing.queue.demo1",
  "msg": "routing test msg info"
}

### 发送mq消息 fanout 模式 -> 广播模式
POST http://localhost:8084/demo/rabbitmq/produce/sendMq
Content-Type: application/json

{
  "exchange": "fanout.exchange.demo",
  "msg": "fanout test fanout msg info -> all"
}

### 发送mq消息 topic 模式 匹配 topic.queue.demo.# 和 topic.queue.*
POST http://localhost:8084/demo/rabbitmq/produce/sendMq
Content-Type: application/json

{
  "exchange": "topic.exchange.demo",
  "routingKey": "topic.queue.demo.yunnuo",
  "msg": "topic test msg info"
}

### 发送mq消息 topic 模式 匹配 topic.queue.*
POST http://localhost:8084/demo/rabbitmq/produce/sendMq
Content-Type: application/json

{
  "exchange": "topic.exchange.demo",
  "routingKey": "topic.queue.demo2.yunnuo",
  "msg": "topic test msg info"
}

### 发送mq消息 Delay 模式
POST http://localhost:8084/demo/rabbitmq/produce/sendDelayMq
Content-Type: application/json

{
  "exchange": "delay.exchange.demo",
  "routingKey": "delay.queue.demo",
  "msg": "Delay test msg info"
}

```

## 消费者

### 直连模式(Direct)

#### 直连模式-路由(Routing)

```java

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


```

#### 直连模式-直连(Simple)

```java

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


```

### 广播模式(Fanout)
```java

/**
 * Fanout 广播模式消费
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-20
 */
@Slf4j
@Component
public class FanoutDemoConsumer {

    @RabbitListener(queues = {RabbitConstants.Queue.FANOUT_MODE_QUEUE_DEMO_1})
    public void handleQueue1(MqMsgStruct msg, Message message, Channel channel) {
        log.info("FanoutDemoConsumer handleQueue1 handle consumerQueue:{}, receivedRoutingKey:{}, receivedExchange:{}, msg:{}", message.getMessageProperties().getConsumerQueue(), message.getMessageProperties().getReceivedRoutingKey(), message.getMessageProperties().getReceivedExchange(), msg);
        ChannelHandlerUtils.basicAckAndRecover(msg, message, channel);
    }

    @RabbitListener(queues = {RabbitConstants.Queue.FANOUT_MODE_QUEUE_DEMO_2})
    public void handleQueue2(MqMsgStruct msg, Message message, Channel channel) {
        log.info("FanoutDemoConsumer handleQueue2 handle consumerQueue:{}, receivedRoutingKey:{}, receivedExchange:{}, msg:{}", message.getMessageProperties().getConsumerQueue(), message.getMessageProperties().getReceivedRoutingKey(), message.getMessageProperties().getReceivedExchange(), msg);
        ChannelHandlerUtils.basicAckAndRecover(msg, message, channel);
    }
}

```

### 主题模式(Topic)

```java


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


```
