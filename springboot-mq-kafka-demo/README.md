# 该项目介绍了docker环境下如何安装kafka和springboot集成kafka实现消息发送和接收

## 前述
> 提供kafka、zooker在docker环境下进行安装的示例，springBoot集成kafka实现producer-生产者和consumer-消费者(监听消费：single模式和batch模式)的功能实现

## 环境安装

```docker

# 拉取镜像
docker pull wurstmeister/zookeeper
docker pull wurstmeister/kafka

# 运行zooker
docker run -d --name zookeeper -p 2181:2181 -t wurstmeister/zookeeper
#运行kafka
docker run -d --name kafka --publish 9092:9092 --link zookeeper --env KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 --env KAFKA_ADVERTISED_HOST_NAME=127.0.0.1 --env KAFKA_ADVERTISED_PORT=9092 --env KAFKA_LOG_DIRS=/kafka/KafkaLog --volume /home/vagrant/kafka/localtime:/etc/localtime --volume /home/vagrant/kafka/log:/app/kafka/log wurstmeister/kafka:latest

```

## SpringBoot集成Kafka消息中间件
### pom依赖

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
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

### 配置
####  yaml文件配置
```yaml

spring:
  kafka:
    # kafka地址
    bootstrap-servers: localhost:9092
    # 生产者配置
    producer:
      # 重试次数
      retries: 3
      #  批量提交
      batch-size: 16384
      # 缓存空间
      buffer-memory: 33554432
    # 消费者配置
    consumer:
      group-id: springboot-mq-kafka-demo
      # 手动提交
      enable-auto-commit: false
      auto-offset-reset: latest
      properties:
        # 超时时间
        session.timeout.ms: 60000
    # 监听
    listener:
      # 类型: single/batch
      type: batch
      log-container-config: false
      # 分区
      concurrency: 5
      # 手动提交
      ack-mode: MANUAL_IMMEDIATE
      
```
#### Config

```java
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Objects;


/**
 * kafka配置类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties({KafkaProperties.class})
@EnableKafka
@AllArgsConstructor
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;

    private static final Integer DEFAULT_PARTITION_NUM = 3;

    private static final String GROUP_ID = "springboot-mq-kafka-demo-batch-manual_immediate";

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    /**
     * kafka 默认 ContainerFactory
     *
     * @return {@link ConcurrentKafkaListenerContainerFactory}
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // 提交模式
        ContainerProperties.AckMode ackMode = Objects.nonNull(kafkaProperties.getListener().getAckMode()) ? kafkaProperties.getListener().getAckMode() : ContainerProperties.AckMode.MANUAL_IMMEDIATE;
        factory.getContainerProperties().setAckMode(ackMode);

        // 分区
        Integer concurrency = kafkaProperties.getListener().getConcurrency();
        concurrency = (Objects.nonNull(concurrency) && concurrency > 0) ? concurrency : DEFAULT_PARTITION_NUM;
        factory.setConcurrency(concurrency);

        // 拉取类型：单个/批量
        KafkaProperties.Listener.Type type = kafkaProperties.getListener().getType();
        factory.setBatchListener(Objects.equals(type, KafkaProperties.Listener.Type.BATCH));

        return factory;
    }


    /**
     * 自定义 ContainerFactory
     *
     * @return {@link ConcurrentKafkaListenerContainerFactory}
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> batchContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setGroupId(GROUP_ID);
        factory.setConcurrency(DEFAULT_PARTITION_NUM);
        factory.setBatchListener(true);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

}

```

### 生产者(producer)
> 下面示例是采用API方式进行调用发送kafka消息，进行模拟生产者

#### 请求DTO

```java

import lombok.Data;

/**
 * kafka 发送 请求
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-12-27
 */
@Data
public class KafkaPushDemoReq {

    private String topic;

    private String msg;

}

```

#### API接口发送kafka
```java

import com.ukayunnuo.core.Result;
import com.ukayunnuo.core.exception.ServiceException;
import com.ukayunnuo.domain.request.KafkaPushDemoReq;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * kafka 测试 api 接口
 *
 * @author yunnuo
 * @since 1.0.0
 */
@RestController
@RequestMapping("/demo/kafka")
public class KafkaPushController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * kafka 发送消息 (无结果)
     *
     * @param req 请求参数
     * @return 结果
     */
    @PostMapping("pushTest")
    public Result<Boolean> pushMsgTest(@RequestBody KafkaPushDemoReq req) {
        kafkaTemplate.send(req.getTopic(), req.getMsg());
        return Result.success(Boolean.TRUE);
    }

}

```
### 消费者(consumer)

```java

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * kafka 消费
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-12-27
 */
@Slf4j
@Component
public class KafkaConsumerTest {


    /**
     * 使用默认配置的 ContainerFactory 进行监听 (single 模式)
     *
     * @param record        消息
     * @param acknowledgment 提交器
     */
    @KafkaListener(topics = {"default_container_factory-test.kafka.demo.default.single"})
    public void receiveMessageDefaultSingle(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        log.info("receiveMessageDefault-single record key:{}, value:{}", record.key(), record.value());
        acknowledgment.acknowledge();
    }

    /**
     * 使用默认配置的 ContainerFactory 进行监听 (batch 模式)
     *
     * @param records        消息
     * @param acknowledgment 提交器
     */
    @KafkaListener(topics = {"default_container_factory-test.kafka.demo.default.batch"})
    public void receiveMessageDefaultBatch(ConsumerRecords<String, String> records, Acknowledgment acknowledgment) {
        log.info("receiveMessageDefault-batch records size:{}", records.count());
        for (ConsumerRecord<String, String> record : records) {
            log.info("receiveMessageDefault-batch record key:{}, value:{}", record.key(), record.value());
        }
        acknowledgment.acknowledge();
    }

    /**
     * 使用自定义的 batchContainerFactory 进行监听
     *
     * @param records        消息
     * @param acknowledgment 提交器
     */
    @KafkaListener(topics = {"batch_container_factory-test.kafka.demo.batch"}, containerFactory = "batchContainerFactory")
    public void receiveMessage(ConsumerRecords<String, String> records, Acknowledgment acknowledgment) {
        log.info("receiveMessage records size:{}", records.count());
        for (ConsumerRecord<String, String> record : records) {
            log.info("receiveMessage record key:{}, value:{}", record.key(), record.value());
        }
        acknowledgment.acknowledge();
    }


}

```

### HTTP请求示例

```http request
### 消息发送-single模式
POST http://localhost:8087/demo/kafka/pushTest
Content-Type: application/json

{
  "topic": "default_container_factory-test.kafka.demo.default.single",
  "msg": "test single send..."
}

### 消息发送-batch模式
POST http://localhost:8087/demo/kafka/pushTest
Content-Type: application/json

{
  "topic": "default_container_factory-test.kafka.demo.default.batch",
  "msg": "test batch send..."
}

### 消息发送-自定义
POST http://localhost:8087/demo/kafka/pushTest
Content-Type: application/json

{
  "topic": "batch_container_factory-test.kafka.demo.batch",
  "msg": "test custom batch send..."
}

```
