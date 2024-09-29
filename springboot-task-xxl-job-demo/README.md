# Springboot 集成 XXL-JOB 框架

XXL-JOB官网文档：https://www.xuxueli.com/xxl-job

## XXL-JOB 介绍

XXL-JOB是一个分布式任务调度平台，其核心设计目标是开发迅速、学习简单、轻量级、易扩展。现已开放源代码并接入多家公司线上产品线，开箱即用。

### 特性

1. 简单：支持通过Web页面对任务进行CRUD操作，操作简单，一分钟上手；
2. 动态：支持动态修改任务状态、启动/停止任务，以及终止运行中任务，即时生效；
3. 调度中心HA（中心式）：调度采用中心式设计，“调度中心”自研调度组件并支持集群部署，可保证调度中心HA；
4. 执行器HA（分布式）：任务分布式执行，任务”执行器”支持集群部署，可保证任务执行HA；
5. 注册中心: 执行器会周期性自动注册任务, 调度中心将会自动发现注册的任务并触发执行。同时，也支持手动录入执行器地址；
6. 弹性扩容缩容：一旦有新执行器机器上线或者下线，下次调度时将会重新分配任务；
7. 触发策略：提供丰富的任务触发策略，包括：Cron触发、固定间隔触发、固定延时触发、API（事件）触发、人工触发、父子任务触发；
8. 调度过期策略：调度中心错过调度时间的补偿处理策略，包括：忽略、立即补偿触发一次等；
9. 阻塞处理策略：调度过于密集执行器来不及处理时的处理策略，策略包括：单机串行（默认）、丢弃后续调度、覆盖之前调度；
10. 任务超时控制：支持自定义任务超时时间，任务运行超时将会主动中断任务；
11. 任务失败重试：支持自定义任务失败重试次数，当任务失败时将会按照预设的失败重试次数主动进行重试；其中分片任务支持分片粒度的失败重试；
12. 任务失败告警；默认提供邮件方式失败告警，同时预留扩展接口，可方便的扩展短信、钉钉等告警方式；
13. 路由策略：执行器集群部署时提供丰富的路由策略，包括：第一个、最后一个、轮询、随机、一致性HASH、最不经常使用、最近最久未使用、故障转移、忙碌转移等；
14. 分片广播任务：执行器集群部署时，任务路由策略选择”分片广播”情况下，一次任务调度将会广播触发集群中所有执行器执行一次任务，可根据分片参数开发分片任务；
15. 动态分片：分片广播任务以执行器为维度进行分片，支持动态扩容执行器集群从而动态增加分片数量，协同进行业务处理；在进行大数据量业务操作时可显著提升任务处理能力和速度。
16. 故障转移：任务路由策略选择”故障转移”情况下，如果执行器集群中某一台机器故障，将会自动Failover切换到一台正常的执行器发送调度请求。
17. 任务进度监控：支持实时监控任务进度；
18. Rolling实时日志：支持在线查看调度结果，并且支持以Rolling方式实时查看执行器输出的完整的执行日志；
19. GLUE：提供Web IDE，支持在线开发任务逻辑代码，动态发布，实时编译生效，省略部署上线的过程。支持30个版本的历史版本回溯。
20. 脚本任务：支持以GLUE模式开发和运行脚本任务，包括Shell、Python、NodeJS、PHP、PowerShell等类型脚本;
21. 命令行任务：原生提供通用命令行任务Handler（Bean任务，”CommandJobHandler”）；业务方只需要提供命令行即可；
22. 任务依赖：支持配置子任务依赖，当父任务执行结束且执行成功后将会主动触发一次子任务的执行, 多个子任务用逗号分隔；
23. 一致性：“调度中心”通过DB锁保证集群分布式调度的一致性, 一次任务调度只会触发一次执行；
24. 自定义任务参数：支持在线配置调度任务入参，即时生效；
25. 调度线程池：调度系统多线程触发调度运行，确保调度精确执行，不被堵塞；
26. 数据加密：调度中心和执行器之间的通讯进行数据加密，提升调度信息安全性；
27. 邮件报警：任务失败时支持邮件报警，支持配置多邮件地址群发报警邮件；
28. 推送maven中央仓库: 将会把最新稳定版推送到maven中央仓库, 方便用户接入和使用;
29. 运行报表：支持实时查看运行数据，如任务数量、调度次数、执行器数量等；以及调度报表，如调度日期分布图，调度成功分布图等；
30. 全异步：任务调度流程全异步化设计实现，如异步调度、异步运行、异步回调等，有效对密集调度进行流量削峰，理论上支持任意时长任务的运行；
31. 跨语言：调度中心与执行器提供语言无关的 RESTful API 服务，第三方任意语言可据此对接调度中心或者实现执行器。除此之外，还提供了
    “多任务模式”和“httpJobHandler”等其他跨语言方案；
32. 国际化：调度中心支持国际化设置，提供中文、英文两种可选语言，默认为中文；
33. 容器化：提供官方docker镜像，并实时更新推送dockerhub，进一步实现产品开箱即用；
34. 线程池隔离：调度线程池进行隔离拆分，慢任务自动降级进入”Slow”线程池，避免耗尽调度线程，提高系统稳定性；
35. 用户管理：支持在线管理系统用户，存在管理员、普通用户两种角色；
36. 权限控制：执行器维度进行权限控制，管理员拥有全量权限，普通用户需要分配执行器权限后才允许相关操作；

## XXL-JOB部署

- 创建数据库`xxl_job`和sql脚本执行

> sql表文件：https://github.com/xuxueli/xxl-job/tree/master/doc/db

### docker部署

```docker
docker run -d \
  --name xxl-job-admin \
  --restart always \
  -v /data/xxl-job/data/logs:/data/applogs \
  -p 8800:8800 \
  -e PARAMS='
    --server.port=8800
    --server.servlet.context-path=/xxl-job-admin
    --spring.datasource.url=jdbc:mysql://172.17.0.1:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
    --spring.datasource.username=[数据库账号]
    --spring.datasource.password=[数据库密码]
    --xxl.job.accessToken=demo-test-token' \
  xuxueli/xxl-job-admin:2.4.0
```

### docker-compose部署

```yaml
version: "3"
services:
  xxl-job-admin:
    restart: always
    image: xuxueli/xxl-job-admin:2.4.0
    container_name: xxl-job-admin
    volumes:
      - /data/xxl-job/data/logs:/data/applogs
    ports:
      - "8800:8800"
    environment:
      PARAMS: '
      --server.port=8800
      --server.servlet.context-path=/xxl-job-admin
      --spring.datasource.url=jdbc:mysql://172.17.0.1:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
      --spring.datasource.username=[数据库账号]
      --spring.datasource.password=[数据库密码]
      --xxl.job.accessToken=demo-test-token'  #代码里面需要指定的token
```

## SpringBoot 集成 XXL-JOB框架

### 1. POM依赖

```xml
        <!--xxl-job-->
        <dependency>
            <groupId>com.xuxueli</groupId>
            <artifactId>xxl-job-core</artifactId>
            <version>${xxl-job.version}</version>
        </dependency>
```
### 2. yaml配置参数
```yaml
xxl:
  job:
    # accessToken 【没有则留空即可】
    access-token: 
    admin:
      address: http://192.168.1.4:8800/xxl-job-admin
    executor:
      # 执行器AppName
      app-name: spring-boot-demo-task-xxl-job-executor
      # 执行器IP [选填]：默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"；
      ip:
      # 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口；
      port: 9999
      # 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径；
      log-path: logs/spring-boot-task-xxl-job-dem/task-log
      # 执行器日志保存天数 [选填] ：值大于3时生效，启用执行器Log文件定期清理功能，否则不生效；
      log-retention-days: -1
```

### 3. 配置类

```java
@Slf4j
@Configuration
@EnableConfigurationProperties(XxlJobProps.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class XxlJobConfig {

    private final XxlJobProps xxlJobProps;

//    @Bean(initMethod = "start", destroyMethod = "destroy") // 低版本(2.2.0以下)使用
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init start.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProps.getAdmin().getAddress());
        xxlJobSpringExecutor.setAccessToken(xxlJobProps.getAccessToken());
        xxlJobSpringExecutor.setAppname(xxlJobProps.getExecutor().getAppName());
        xxlJobSpringExecutor.setIp(xxlJobProps.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(xxlJobProps.getExecutor().getPort());
        xxlJobSpringExecutor.setLogPath(xxlJobProps.getExecutor().getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProps.getExecutor().getLogRetentionDays());

        log.info(">>>>>>>>>>> xxl-job config init info :{}", xxlJobProps);
        return xxlJobSpringExecutor;
    }

}

```

### 4. 测试
```java
@Slf4j
@Component
public class XxlJobTaskDemo {

    @XxlJob("demoJobHandler")
    public Result<String> demoJobHandler(String param) {
        log.info("xxl-job demo task execute start! param:{}", param);
        return Result.success("xxl-job demo task execute success");
    }

}

```