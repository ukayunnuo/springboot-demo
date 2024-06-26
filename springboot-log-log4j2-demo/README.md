# 该项目介绍springboot集成log4j2实现自定义格式日志打印的功能

## pom依赖
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <!-- 排除自带的logging -->
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <!-- 排除自带的logging -->
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- log4j2 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-web</artifactId>
        </dependency>
        <!-- disruptor -->
        <dependency>
            <groupId>com.lmax</groupId>
            <artifactId>disruptor</artifactId>
            <version>3.4.4</version>
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

```

## 配置
### yml配置
```yaml

# 指定使用哪个日志配置文件
logging:
  config: classpath:log4j2.xml

```
## xml配置日志具体内容
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE >ALL -->
<!--Configuration后面的status，这个用于设置log4j2自身内部的信息输出，可以不设置，当设置成trace时，你会看到log4j2内部各种详细输出；可以设置成OFF(关闭)或Error(只输出错误信息)-->
<!--monitorInterval：Log4j2能够自动检测修改配置文件和重新配置本身，设置间隔秒数-->
<Configuration status="WARN" monitorInterval="30">

    <Properties>
        <!-- 缺省配置（用于开发环境），配置日志文件输出目录和动态参数。其他环境需要在VM参数中指定；
       “sys:”表示：如果VM参数中没指定这个变量值，则使用本文件中定义的缺省全局变量值 -->
        <Property name="instance">spring-boot-demo-log</Property>
        <Property name="log.dir">spring-boot-demo-logs</Property>
        <property name="log.pattern">
            {"time":"%d{MM-dd HH:mm:ss.SSS}", "level":"%level", "thread":"%t", "method":"%method", "class":"%class", "line":"%class#%method:%line", "message":"%enc{%m}{JSON}", "stack_trace":"%exception{15}"}%n
        </property>
        <property name="consoleLogPattern">
            %highlight{[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%t] \t [%level] [%class#%method:%line]: %m%n}
        </property>
    </Properties>

    <!-- 定义所有的appender -->
    <Appenders>
        <!--这个输出控制台的配置-->
        <Console name="Console" target="SYSTEM_OUT">
            <!--输出日志的格式-->
            <PatternLayout pattern="${consoleLogPattern}"/>
        </Console>

        <!-- info及以上级别的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档-->
        <RollingRandomAccessFile name="infoLog"
                                 fileName="${log.dir}/${instance}-info.log"
                                 filePattern="${log.dir}/%d{yyyy-MM}/${instance}-info-%d{yyyy-MM-dd}-%i.log.gz"
                                 append="true">
            <PatternLayout pattern="${log.pattern}"/>

            <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <Filters>
                <!-- onMatch="ACCEPT" 表示匹配该级别及以上 -->
                <!-- onMatch="DENY" 表示不匹配该级别及以上-->
                <!-- onMatch="NEUTRAL" 表示该级别及以上的，由下一个filter处理，如果当前是最后一个，则表示匹配该级别及以上-->
                <!-- onMismatch="ACCEPT" 表示匹配该级别以下-->
                <!-- onMismatch="NEUTRAL" 表示该级别及以下的，由下一个filter处理，如果当前是最后一个，则不匹配该级别以下的-->
                <!-- onMismatch="DENY" 表示不匹配该级别以下的-->
                <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="NEUTRAL"/>
            </Filters>

            <Policies>
                <!-- 基于时间的滚动策略，interval属性用来指定多久滚动一次，默认是1 hour -->
                <TimeBasedTriggeringPolicy interval="1" modulate="true"/>
                <!-- 基于指定文件大小的滚动策略，size属性用来定义每个日志文件的大小 -->
                <SizeBasedTriggeringPolicy size="1MB"/>
                <!-- DefaultRolloverStrategy:用来指定同一个文件夹下最多有几个日志文件时开始删除最旧的，创建新的(通过max属性) -->
            </Policies>
        </RollingRandomAccessFile>


        <!-- warn级别的日志信息 -->
        <RollingRandomAccessFile name="warnLog"
                                 fileName="${log.dir}/${instance}-warn.log"
                                 filePattern="${log.dir}/%d{yyyy-MM}/${instance}-warn-%d{yyyy-MM-dd}-%i.log.zip"
                                 append="true">
            <Filters>
                <!-- onMatch="ACCEPT" 表示匹配该级别及以上 -->
                <!-- onMatch="DENY" 表示不匹配该级别及以上-->
                <!-- onMatch="NEUTRAL" 表示该级别及以上的，由下一个filter处理，如果当前是最后一个，则表示匹配该级别及以上-->
                <!-- onMismatch="ACCEPT" 表示匹配该级别以下-->
                <!-- onMismatch="NEUTRAL" 表示该级别及以下的，由下一个filter处理，如果当前是最后一个，则不匹配该级别以下的-->
                <!-- onMismatch="DENY" 表示不匹配该级别以下的-->
                <ThresholdFilter level="error" onMatch="DENY" onMismatch="NEUTRAL"/>
                <ThresholdFilter level="warn" onMatch="ACCEPT" onMismatch="DENY"/>
            </Filters>

            <PatternLayout pattern="log.pattern"/>

            <Policies>
                <TimeBasedTriggeringPolicy interval="1" modulate="true"/>
                <SizeBasedTriggeringPolicy size="1MB"/>
            </Policies>
        </RollingRandomAccessFile>


        <!-- error级别的日志信息 -->
        <RollingRandomAccessFile name="errorLog"
                                 fileName="${log.dir}/${instance}-error.log"
                                 filePattern="${log.dir}/%d{yyyy-MM}/${instance}-error-%d{yyyy-MM-dd}-%i.log.zip"
                                 append="true">
            <Filters>
                <!-- onMatch="ACCEPT" 表示匹配该级别及以上 -->
                <!-- onMatch="DENY" 表示不匹配该级别及以上-->
                <!-- onMatch="NEUTRAL" 表示该级别及以上的，由下一个filter处理，如果当前是最后一个，则表示匹配该级别及以上-->
                <!-- onMismatch="ACCEPT" 表示匹配该级别以下-->
                <!-- onMismatch="NEUTRAL" 表示该级别及以下的，由下一个filter处理，如果当前是最后一个，则不匹配该级别以下的-->
                <!-- onMismatch="DENY" 表示不匹配该级别以下的-->
                <ThresholdFilter level="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
            </Filters>

            <PatternLayout pattern="log.pattern"/>

            <Policies>
                <TimeBasedTriggeringPolicy interval="1" modulate="true"/>
                <SizeBasedTriggeringPolicy size="1MB"/>
            </Policies>
        </RollingRandomAccessFile>
    </Appenders>


    <!-- 全局配置，默认所有的Logger都继承此配置 -->

    <!-- 用来配置LoggerConfig，包含一个root logger和若干个普通logger。
         additivity指定是否同时输出log到父类的appender，缺省为true。
         一个Logger可以绑定多个不同的Appender。只有定义了logger并引入的appender，appender才会生效。
    -->
    <Loggers>
        <!-- 第三方的软件日志级别 -->
        <logger name="org.springframework" level="info" additivity="true">
            <AppenderRef ref="warnLog"/>
            <AppenderRef ref="errorLog"/>
        </logger>

        <logger name="java.sql.PreparedStatement" level="debug" additivity="true">
            <AppenderRef ref="Console"/>
        </logger>

        <logger name="codex.terry.filter" level="debug" additivity="true">
            <AppenderRef ref="infoLog"/>
            <AppenderRef ref="warnLog"/>
            <AppenderRef ref="errorLog"/>
        </logger>

        <!-- AsyncRoot - 异步记录日志 - 需要LMAXDisruptor的支持, 如果不想使用AsyncRoot，则可以放开下面的 Root -->
        <AsyncRoot level="info" includeLocation="true">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="infoLog"/>
            <AppenderRef ref="errorLog"/>
        </AsyncRoot>

<!--        <Root level="info" includeLocation="true">-->
<!--            <AppenderRef ref="Console"/>-->
<!--            <AppenderRef ref="infoLog"/>-->
<!--            <AppenderRef ref="errorLog"/>-->
<!--        </Root>-->

        <!-- root logger 配置 -->
        <!--        <Root level="ALL" includeLocation="true">-->
        <!--            <AppenderRef ref="Console"/>-->
        <!--        </Root>-->


    </Loggers>

</Configuration>


```
## 测试
```java

@Slf4j
public class PrintLogTest {

    public static void main(String[] args) {
        log.info("=============================日志测试打印======================================");

        log.info("日志测试-->INFO");
        log.warn("日志测试-->WARN");
        log.error("日志测试-->ERROR");

        Map<String, Object> map = new HashMap<>(3);
        map.put("name", "yunnuo");
        map.put("age", 23);
        map.put("email", "2552846359@qq.com");
        log.info("author info:{}", JSONObject.toJSONString(map));
    }

}

```
