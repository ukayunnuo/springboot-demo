# 该项目介绍springboot使用logback集成logstash 实现日志上报給logstash, 实现ELK日志采集

## pom依赖
```xml

<properties>
        <java.version>1.8</java.version>
        <lombok.version>1.18.28</lombok.version>
        <fastjson2.version>2.0.34</fastjson2.version>
        <junit.version>4.13.2</junit.version>
        <logstash.version>7.2</logstash.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
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

        <dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
            <version>${logstash.version}</version>
        </dependency>

    </dependencies>

```

## yaml配置
```yaml

server:
  port: 8080

spring:
  application:
    name: springboot-log-logstash-logback-demo
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
    default-property-inclusion: non_null

# 日志配置
logging:
  level:
    org.springframework: warn
  config: classpath:logback-spring.xml
  # logstash 配置
  logstash:
    url: 127.0.0.1:4560


```
注意：需要文件名需要为logback-spring.xml ，不然在配置日志参数时，会报错无法获取到yaml的配置


## logback-spring.xml配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <property name="log.path" value="/data/logs/springboot-log-logback-demo"/>
    <property name="console.log.pattern"
              value="%green(%d{yyyy-MM-dd HH:mm:ss}) %highlight([%level]) %boldMagenta(${PID}) --- %green([%thread])  %boldMagenta(%class) - [%method,%line]: %msg%n"/>
    <property name="log.pattern"
              value="%d{yyyy-MM-dd HH:mm:ss} [%level] ${PID} --- [%thread] %class - [%method,%line]: %msg%n"/>

    <springProperty name="LOG_STASH_URL" scope="context" source="logging.logstash.url" defaultValue="127.0.0.1:4560"/>
    <springProperty name="app" scope="context" source="spring.application.name" defaultValue="springboot-server"/>


    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${console.log.pattern}</pattern>
            <charset>utf-8</charset>
        </encoder>
    </appender>

    <appender name="file_out" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${log.path}/out.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/out.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>10</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${log.pattern}</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
        </filter>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
        </filter>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
        </filter>
    </appender>

    <appender name="async_out" class="ch.qos.logback.classic.AsyncAppender">
        <discardingThreshold>0</discardingThreshold>
        <queueSize>512</queueSize>
        <appender-ref ref="file_out"/>
    </appender>

    <!-- Logstash -->
    <appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>${LOG_STASH_URL}</destination>
        <!-- logstash默认输出格式  -->
        <!-- <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder">
             <customFields>{"app":"${app}"}</customFields>
             <pattern>${log.json.pattern}</pattern>
         </encoder>-->

        <!-- 自定义logstash输出格式 - json-->
        <encoder charset="UTF-8" class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <pattern>
                    <pattern>
                        {
                        "app":"${app}",
                        "timestamp": "%d{MM-dd HH:mm:ss.SSS}",
                        "level": "%level",
                        "class": "%class",
                        "method": "%method",
                        "line": "%class#%method - %line",
                        "message": "%msg",
                        "header.client-ip": "%X{header.client-ip}",
                        "header.content-length": "%X{header.content-length}",
                        "thread": "%thread",
                        "stack_trace": "%exception{10}"
                        }
                    </pattern>
                </pattern>
            </providers>
        </encoder>
    </appender>


    <root level="info">
        <appender-ref ref="console"/>
        <appender-ref ref="async_out"/>
        <appender-ref ref="logstash"/>
    </root>

</configuration>


```

## logstash配置

```

# 设置监听端口，格式为json格式
input {
  tcp {
    mode => "server"
    host => "0.0.0.0"
    port => 4560
    codec => json
  }
}

# 输出到es中
output {
  elasticsearch {
    hosts => "elasticsearch:9200"
    index => "logstash-%{app}-%{+YYYY.MM.dd}"
  }
  # 输出到控制台以便调试
  # stdout { codec => rubydebug }
}

```

