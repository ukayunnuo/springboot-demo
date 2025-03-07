# 该项目介绍了docker环境下如何安装nacos和springboot集成nacos服务实现动态配置更新

## 前述

Nacos 是阿里巴巴开源的一个动态服务发现、配置管理和服务管理平台，旨在帮助开发者更轻松地构建云原生应用。它提供了以下几个核心功能：
1. 服务发现与管理：Nacos支持基于DNS和HTTP的接口来实现服务的注册和发现。服务提供者可以向Nacos注册自身提供的服务，而服务消费者可以从Nacos获取所需的服务列表，并根据实际情况进行调用。
2. 动态配置服务：Nacos允许您集中管理应用程序的配置，并能够在不重启应用的情况下实时更新配置。这使得应用程序能够快速响应变化，非常适合于需要频繁调整配置的场景。
3. 动态DNS服务：Nacos还提供了动态DNS服务，支持权重路由等功能，可以帮助您更容易地实现负载均衡和故障转移，提高系统的可用性和灵活性。
4. 易于使用的控制台：Nacos提供了一个简单直观的Web界面，便于用户管理和调试服务和配置。通过这个界面，您可以查看和管理所有的服务注册信息以及配置项。
5. 多环境和多租户支持：Nacos设计时考虑了多环境（如开发、测试、生产）和多租户的需求，支持命名空间和分组等概念来隔离不同环境或项目之间的配置和服务。

简而言之，Nacos是一个强大的工具，适用于微服务架构下的服务治理和配置管理，帮助简化开发过程，提升运维效率。
## 环境安装

### docker安装nacos

1. 先启动容器（目的拷贝配置文件）

```bash
# 创建目录
mkdir -p /data/docker/nacos

docker run --name nacos-server -d nacos/nacos-server:v2.3.2

# 拷贝
docker cp nacos-server:/home/nacos/conf /data/docker/nacos/ 
docker cp nacos-server:/home/nacos/logs /data/docker/nacos/ 
docker cp nacos-server:/home/nacos/bin /data/docker/nacos/ 
docker cp nacos-server:/home/nacos/data /data/docker/nacos/

# 停止删除
docker rm -f nacos-server
```

- 添加配置表sql数据文件

```bash
# 位置
/data/nacos/conf/mysql-schema.sql
```

- 修改配置文件application.properties
```bash
#生成nacos.core.auth.plugin.nacos.token.secret.key
openssl rand -hex 32 #对结果进行base64编码
# ZWVmYjEyNDE5YzYwMGUyNTZjNmJiZDg0ZmI0YWE1ODFhYmJhMTYyODEyNjMxN2FiMTUxYWQ3OWQ3M2ViNzNlMg==

vi /data/docker/nacos/conf/application.properties
 
```

- 配置docker-startup.sh(修改jvm参数)
```bash
#所在位置
/data/nacos/bin/docker-startup.sh 
```

2. 运行容器

- (docker) 版本
```docker

docker run --name nacos-sever \
-e MODE=standalone \
-e SPRING_DATASOURCE_PLATFORM=mysql \
-e MYSQL_SERVICE_HOST=10.61.19.128 \
-e MYSQL_SERVICE_DB_NAME=nacos_config \
-e MYSQL_SERVICE_PORT=3306 \
-e MYSQL_SERVICE_USER=root \
-e MYSQL_SERVICE_PASSWORD=root \
-e NACOS_AUTH_ENABLE=true \
-e NACOS_AUTH_TOKEN=ZWVmYjEyNDE5YzYwMGUyNTZjNmJiZDg0ZmI0YWE1ODFhYmJhMTYyODEyNjMxN2FiMTUxYWQ3OWQ3M2ViNzNlMg== \
-e NACOS_AUTH_IDENTITY_KEY=100 \
-e NACOS_AUTH_IDENTITY_VALUE=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjpbeyJ4eHh4IjoieXl5In1dLCJpYXQiOjE2Nzk1NzU0NzIsImV4cCI6MTY3OTUwMDc5OSwiYXVkIjoiIiwiaXNzIjoiIiwic3ViIjoiIn0.nhN_hKcnjlX0QW-kQj2beLehBzrQnB1IhhJZe2WO-c0 \
-v /data/docker/nacos/conf:/home/nacos/conf \
-v /data/docker/nacos/logs:/home/nacos/logs \
-v /data/docker/nacos/bin:/home/nacos/bin \
-v /data/docker/nacos/data:/home/nacos/data \
-p 8488:8848 -p 9848:9848 -d nacos/nacos-server:v2.3.2

```
- docker-compose 安装
```yaml

version: '3'
services:
  nacos:
    image: nacos/nacos-server:v2.3.2
    container_name: nacos-server
    restart: always
    environment:
      - PREFER_HOST_MODE=hostname
      - MODE=standalone
      - SPRING_DATASOURCE_PLATFORM=mysql
      - MYSQL_SERVICE_HOST=172.17.0.1
      - MYSQL_SERVICE_DB_NAME=nacos_config
      - MYSQL_SERVICE_PORT=3306
      - MYSQL_SERVICE_USER=root
      - MYSQL_SERVICE_PASSWORD=root
      - NACOS_AUTH_ENABLE=false  # false=运行匿名访问
      - NACOS_AUTH_CACHE_ENABLE=false # false=运行匿名访问
      - NACOS_AUTH_TOKEN=ZWVmYjEyNDE5YzYwMGUyNTZjNmJiZDg0ZmI0YWE1ODFhYmJhMTYyODEyNjMxN2FiMTUxYWQ3OWQ3M2ViNzNlMg==
      - NACOS_AUTH_IDENTITY_KEY=100
      - NACOS_AUTH_IDENTITY_VALUE=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjpbeyJ4eHh4IjoieXl5In1dLCJpYXQiOjE2Nzk1NzU0NzIsImV4cCI6MTY3OTUwMDc5OSwiYXVkIjoiIiwiaXNzIjoiIiwic3ViIjoiIn0.nhN_hKcnjlX0QW-kQj2beLehBzrQnB1IhhJZe2WO-c0
    ports:
      - 8848:8848
      - 9848:9848
    volumes:
      - /data/docker/nacos/conf:/home/nacos/conf
      - /data/docker/nacos/logs:/home/nacos/logs
      - /data/docker/nacos/bin:/home/nacos/bin
      - /data/docker/nacos/data:/home/nacos/data

```



## SpringBoot集成nacos
### pom依赖
```xml
<properties>
    <nacos.version>2021.0.5.0</nacos.version>
</properties>

        <!-- Spring Cloud Alibaba Nacos Config -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        <version>${nacos.version}</version>
    </dependency>

```

### 配置文件
```yaml
# nacos 配置
spring:
  cloud:
    nacos:
      # nacos 服务地址
      server-addr: 10.61.19.128:8848
      username: nacos
      password: gzbjk@nacos2024
      discovery:
        # 注册组
        group: DEFAULT_GROUP
        namespace: public
      config:
        # 配置组
        group: ${spring.cloud.nacos.discovery.group}
        namespace: ${spring.cloud.nacos.discovery.namespace}
  config:
    import:
      - optional:nacos:application-common.yml
      - optional:nacos:${spring.application.name}.yml
```

### 测试动态配置更新

#### nacos中新建配置
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/75223801f74942dd99a0a2f0b8beb2ae.png)


#### 配置类
```java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * nacos 动态配置更新测试配置
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Data
@RefreshScope // 动态刷新配置
@Component
@ConfigurationProperties(prefix = "test")
public class TestDynamicConfig {

    private String name;

    private Double version;

}


```

#### 模拟定时获取配置
```java

import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.config.TestDynamicConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 动态配置更新测试
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Component
public class TestDynamicConfigTest {

    @Resource
    private TestDynamicConfig testDynamicConfig;

    @Scheduled(fixedDelay = 5000)
    public void test() {
        log.info("testDynamicConfig config:{}", JSONObject.toJSONString(testDynamicConfig));
    }

}

```

#### 修改配置后，通过日志查看变更记录

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/75223801f74942dd99a0a2f0b8beb2ae.png)
