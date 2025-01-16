# 该项目介绍了docker环境下如何安装mqtt和springboot集成mqtt服务

## 前述

> MQTT（Message Queuing Telemetry Transport）是一种轻量级的消息传输协议，设计用于在资源受限的设备和低带宽、不可靠的网络连接中高效地传输数据。主要用于物联网设备传输，设备之间可以高效地交换数据

## 环境安装

### docker安装mqtt

1. 添加配置文件

```bash
mkdir -p /data/docker/mosquitto/config

vi /data/docker/mosquitto/config/mosquitto.conf

chmod -R 777 /data/docker/mosquitto
```

- mosquitto.conf配置文件内容

```bash
persistence true
persistence_location /mosquitto/data
log_dest file /mosquitto/log/mosquitto.log

port 1883
listener 9001
protocol websockets

# allow_anonymous false
# password_file /mosquitto/config/pwfile.conf
```

注意：先注释密码验证，后面设置密码后，再开放

2. 运行容器

```docker

docker run -d \
  --name mosquitto-mqtt \
  --privileged \
  --restart always \
  -p 1883:1883 \
  -p 9001:9001 \
  -v /data/docker/mosquitto/config:/mosquitto/config \
  -v /data/docker/mosquitto/data:/mosquitto/data \
  -v /data/docker/mosquitto/log:/mosquitto/log \
  eclipse-mosquitto:2


```

3. 进入容器 创建密码文件

```bash
# 进入容器
docker exec -it mosquitto-mqtt sh

# 创建密码文件并设置权限
touch /mosquitto/config/pwfile.conf
chmod 0700 /mosquitto/config/pwfile.conf

# 设置账号密码 为 admin mqtt2024
mosquitto_passwd -b /mosquitto/config/pwfile.conf admin mqtt2024 
```

4. 配置文件放开注释密码验证

```bash
allow_anonymous false
password_file /mosquitto/config/pwfile.conf
```

5. 重启docker 容器


## SpringBoot集成mqtt
### pom依赖
```xml
<properties>
    <mqtt.version>5.4.4</mqtt.version>
</properties>

        <!-- mqtt -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-integration</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.integration</groupId>
            <artifactId>spring-integration-mqtt</artifactId>
            <version>${mqtt.version}</version>
        </dependency>
```

### 配置
####  yaml文件配置
```yaml
spring:
  # MQTT配置信息
  mqtt:
    # 是否启用mqtt功能
    enabled: true
    # MQTT服务地址（多个用逗号隔开）
    url: tcp://10.61.19.128:1883
    # 用户名
    username: admin
    # 密码
    password: mqtt2024
    # 客户端id(不允许重复)
    client-id: test-client-1
    # 默认推送主题（多个用逗号隔开）
      # 单级通配符 (+)：匹配一个层级的主题。例如，sensor/+/temperature 可以匹配 sensor/room1/temperature 和 sensor/room2/temperature。
      # 多级通配符 (#)：匹配多个层级的主题。例如，sensor/# 可以匹配 sensor/room1/temperature、sensor/room2/humidity 等
    default-topic: test-topic/#,test-topic-cc/+
    #    default-topic: test-topic/1,test-topic/2
    # 超时时间 （单位：秒）
    timeout: 100
    # 心跳 （单位：秒）
    keepalive: 60
```

#### 核心配置类
```java
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import javax.annotation.Resource;

/**
 * mqtt 配置类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
public class MqttConfig {

    @Resource
    private MqttProperties mqttProperties;


    /**
     * 连接配置
     *
     * @return 配置
     */
    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setServerURIs(mqttProperties.getServerURIs());
        options.setUserName(mqttProperties.getUsername());
        options.setPassword(mqttProperties.getPassword().toCharArray());
        options.setConnectionTimeout(mqttProperties.getTimeout());
        options.setKeepAliveInterval(mqttProperties.getKeepalive());
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(mqttProperties.getKeepalive());
        return options;
    }

    /**
     * 连接工厂
     *
     * @return 工厂
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions mqttConnectOptions = mqttConnectOptions();
        factory.setConnectionOptions(mqttConnectOptions);
        log.info("初始化 MQTT 配置:{}", JSONObject.toJSONString(mqttConnectOptions));
        return factory;
    }

}

```

#### 生产者配置类（配置生产者信息）
```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

/**
 * 生产者配置
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
public class MqttOutboundConfiguration {

    @Resource
    private MqttProperties mqttProperties;

    @Resource
    private MqttConfig mqttConfig;

    public static final String CLIENT_SUFFIX_PRODUCER = "_producer";

    /**
     * MQTT信息通道（生产者）
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        // 单线程- Spring Integration默认的消息通道，它允许将消息发送给一个订阅者，然后阻碍发送直到消息被接收。
        // return new DirectChannel();

        // 多线程
        return new ExecutorChannel(Executors.newFixedThreadPool(10));
    }

    /**
     * MQTT消息处理器（生产者）
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        // 客户端id
        String clientId = mqttProperties.getClientId();
        // 默认主题
        String defaultTopic = mqttProperties.getDefaultTopic();
        MqttPahoClientFactory mqttPahoClientFactory = mqttConfig.mqttClientFactory();

        // 发送消息和消费消息Channel可以使用相同MqttPahoClientFactory
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + CLIENT_SUFFIX_PRODUCER, mqttPahoClientFactory);
        // true，异步，发送消息时将不会阻塞。
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(defaultTopic);
        // 默认QoS
        messageHandler.setDefaultQos(1);
        // Paho消息转换器
        DefaultPahoMessageConverter defaultPahoMessageConverter = new DefaultPahoMessageConverter();
        // defaultPahoMessageConverter.setPayloadAsBytes(true);
        // 发送默认按字节类型发送消息
        messageHandler.setConverter(defaultPahoMessageConverter);
        return messageHandler;
    }

}
```

#### 生产者处理类（封装发送消息的方法）
```java
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * 生产者处理器
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {

    /**
     * 发送mqtt消息
     *
     * @param topic   主题
     * @param payload 消息体 字符串
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

    /**
     * 发送包含qos的消息
     *
     * @param topic   主题
     * @param qos     消息处理机制
     * @param payload 消息体 字符串
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);

    /**
     * 发送包含qos的消息
     *
     * @param topic   主题
     * @param qos     消息处理机制
     * @param payload 消息体 字节
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, byte[] payload);

}
```

#### 消息发送工具类
```java
import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.gateway.MqttGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * mqtt 工具类
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
public class MqttProducerUtil {

    @Resource
    private MqttGateway mqttGateway;


    /**
     * 发送mqtt消息
     *
     * @param topic   主题
     * @param message 内容
     */
    public void send(String topic, String message) {
        mqttGateway.sendToMqtt(topic, message);
    }

    /**
     * 发送mqtt消息
     *
     * @param topic       主题
     * @param qos         消息处理机制
     * @param messageBody 消息体
     */
    public void send(String topic, int qos, String messageBody) {
        mqttGateway.sendToMqtt(topic, qos, messageBody);
    }

    /**
     * 发送mqtt消息
     *
     * @param topic       主题
     * @param qos         消息处理机制
     * @param messageBody 消息体
     */
    public <T> void sendToJsonStr(String topic, int qos, T messageBody) {
        mqttGateway.sendToMqtt(topic, qos, JSONObject.toJSONString(messageBody));
    }

    /**
     * 发送mqtt消息
     *
     * @param topic   主题
     * @param qos     消息处理机制
     * @param message 消息体
     */
    public void send(String topic, int qos, byte[] message) {
        mqttGateway.sendToMqtt(topic, qos, message);
    }

}

```

#### 发送消息测试类
```java
import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.core.MqttMsgStruct;
import com.ukayunnuo.enums.MqttQos;
import com.ukayunnuo.utils.MqttProducerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@RestController
@RequestMapping("/demo/mqtt/produce")
public class MqttProduceController {

    @Autowired(required = false)
    private MqttProducerUtil mqttProducerUtil;

    @PostMapping("/sendMsg")
    public Result<Boolean> deleteExchange(@RequestBody MqttMsgStruct msgStruct) {
        mqttProducerUtil.send(msgStruct.getTopic(), Objects.nonNull(msgStruct.getQos()) ? msgStruct.getQos().code : MqttQos.QOS_0.code, msgStruct.getMessage());
        return Result.success(Boolean.TRUE);
    }
}


```

#### 消费者配置类(配置消费者信息)
```java
import com.ukayunnuo.handler.MqttMessageReceiverHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

/**
 * 消费者配置
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
@Configuration
@IntegrationComponentScan
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
public class MqttInboundConfiguration {


    @Resource
    private MqttProperties mqttProperties;

    @Resource
    private MqttConfig mqttConfig;


    private MqttMessageReceiverHandler mqttMessageReceiverHandler;

    public static final String CLIENT_SUFFIX_CONSUMERS = "_consumer";


    /**
     * MQTT信息通道（消费者）
     *
     * @return 消息同道
     */
    @Bean
    public MessageChannel mqttInBoundChannel() {
        // 单线程- Spring Integration默认的消息通道，它允许将消息发送给一个订阅者，然后阻碍发送直到消息被接收。
        // return new DirectChannel();

        // 多线程
        return new ExecutorChannel(Executors.newFixedThreadPool(10));

    }

    /**
     * mqtt入站消息处理器（用于指定消息入站通道接收到生产者生产的消息后处理消息）
     *
     * @return 消息处理器
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInBoundChannel")
    public MessageHandler mqttMessageHandler() {
        return this.mqttMessageReceiverHandler;
    }

    /**
     * MQTT消息订阅绑定（消费者）
     * @return 消息订阅
     */
    @Bean
    public MessageProducerSupport mqttInbound() {
        MqttPahoClientFactory mqttPahoClientFactory = mqttConfig.mqttClientFactory();

        // Paho客户端消息驱动通道适配器，主要用来订阅主题
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                mqttProperties.getClientId() + CLIENT_SUFFIX_CONSUMERS,
                mqttPahoClientFactory,
                mqttProperties.getTopics()
        );
//        adapter.setCompletionTimeout(5000L);
        // Paho消息转换器
        DefaultPahoMessageConverter defaultPahoMessageConverter = new DefaultPahoMessageConverter();
        // 按字节接收消息
        // defaultPahoMessageConverter.setPayloadAsBytes(true);
        adapter.setConverter(defaultPahoMessageConverter);
        // 设置QoS
        adapter.setQos(1);
        // 设置订阅通道
        adapter.setOutputChannel(mqttInBoundChannel());
        return adapter;
    }

}
```

#### 消费者处理类（处理接收的消息）
```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 消费者处理器
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "true")
public class MqttMessageReceiverHandler implements MessageHandler {

    /**
     * 消息处理
     *
     * @param message 消息
     * @throws MessagingException 消息异常
     */
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();
        // topic
        String topic = Objects.requireNonNull(headers.get(MqttHeaders.RECEIVED_TOPIC)).toString();
        try {
            // 获取消息体
            String msg;
            if (message.getPayload() instanceof String) {
                msg = message.getPayload().toString();
            } else {
                msg = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
            }
            log.info("mqtt handleMessage 接收到消息 topic:{}, msg：{} ", topic, msg);

        } catch (Exception e) {
            log.error("handleMessage 处理mqtt消息错误! message:{}, e:{}", message, e.getMessage(), e);
        }
    }
}
```



### 发送、接收消息测试

- 发送消息请求
```http request
POST http://localhost:8010/demo/mqtt/produce/sendMsg
Content-Type: application/json

{
  "topic": "test-topic/test/1",
  "qos": "QOS_0",
  "message": "test msg..."
}
```


- 接收消息日志
```log
2025-01-16 15:26:54.657  INFO 39660 --- [pool-1-thread-1] c.u.handler.MqttMessageReceiverHandler   : mqtt handleMessage 接收到消息 topic:test-topic/2, msg：test msg... 
2025-01-16 15:27:26.076  INFO 39660 --- [pool-1-thread-2] c.u.handler.MqttMessageReceiverHandler   : mqtt handleMessage 接收到消息 topic:test-topic/test/1, msg：test msg... 
```
