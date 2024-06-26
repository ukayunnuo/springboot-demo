# springboot-orm-mybatis-plus-demo

## 介绍

本模块利用springBoot 集成了以下功能插件：

- MyBaits plus orm插件
- fastjson2
- lombok
- hutool工具类
- HikariCP
- p6spy
- dynamic 动态数据库插件
- pagehelper 分页插件

## pom依赖
```xml

<properties>
    <java.version>1.8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <HikariCP.version>4.0.3</HikariCP.version>
    <dynamic-datasource.version>3.5.0</dynamic-datasource.version>
    <p6spy.version>3.9.1</p6spy.version>
    <mybatis-plus.version>3.5.3.2</mybatis-plus.version>
    <mysql-connector.version>8.0.33</mysql-connector.version>
    <lombok.version>1.18.28</lombok.version>
    <fastjson2.version>2.0.34</fastjson2.version>
    <hutool.version>5.8.21</hutool.version>
    <pagehelper.version>2.1.0</pagehelper.version>
</properties>
<dependencys>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- 热部署插件 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>${mysql-connector.version}</version>
    </dependency>
    
    <!-- https://mvnrepository.com/artifact/com.baomidou/dynamic-datasource-spring-boot-starter -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
        <version>${dynamic-datasource.version}</version>
    </dependency>
    
    <!-- https://mvnrepository.com/artifact/p6spy/p6spy -->
    <dependency>
        <groupId>p6spy</groupId>
        <artifactId>p6spy</artifactId>
        <version>${p6spy.version}</version>
    </dependency>
    
    <!-- MyBaits plus 插件 -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>${mybatis-plus.version}</version>
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
    
    <!-- pagehelper -->
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>${pagehelper.version}</version>
    </dependency>
</dependencys>

```

## mybatis-plus 配置
### yaml 配置
```yaml

# mybatis-plus 配置
mybatis-plus:
  # xml扫描，多个目录用逗号或者分号分隔（告诉 Mapper 所对应的 XML 文件位置）
  mapper-locations: classpath:**/*Mapper.xml
  global-config:
    db-config:
      # 1 代表已删除，不配置默认是1，也可修改配置
      logic-delete-value: 1
      # 0 代表未删除，不配置默认是0，也可修改配置
      logic-not-delete-value: 0
      # 添加非空判断
      field-strategy: not_empty
      # 是否开启表名自动驼峰命名规则
      table-underline: true
  # 日志配置
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    # 是否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN（下划线命名） 到经典 Java 属性名 aColumn（驼峰命名） 的类似映射
    map-underscore-to-camel-case: true
    # 开启二级缓存
#    cache-enabled: true


```
### config配置
```java

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus 配置类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-03
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 插件
     *
     * @return {@link MybatisPlusInterceptor}
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}

```

## pagehelper配置
```java

import com.github.pagehelper.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PageHelper 配置类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-03
 */
@Configuration
public class PageHelperConfig {

    /**
     * pagehelper 拦截器
     *
     * @return {@link PageInterceptor}
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

}

```

## 使用教程

### 1. 创建数据库和User表

SQL文件位置：`src/main/resources/sql/user.sql`

### 2. 修改配置文件连接数据库参数

配置文件位置：

- `src/main/resources/application-durid.yml`
- `src/main/resources/application-durid-p6spy.yml`

```yaml
spring:
  datasource:
    #动态数据源DS 配置
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #严格匹配数据源,默认false. true未匹配到指定数据源时抛异常,false使用默认数据源
      datasource:
        master: # 默认数据源
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://${SPRINGBOOT_DEMO_MASTER_IP:127.0.0.1}:${SPRINGBOOT_DEMO_MASTER_PORT:3306}/KBK?createDatabaseIfNotExist=true&useSSL=false&useUnicode=true&characterEncoding=utf8
          username: ${SPRINGBOOT_DEMO_MASTER_USER:root}
          password: ${SPRINGBOOT_DEMO_MASTER_PASSWD:root}

```

### 3. 启动和测试

启动：`DatabaseDemoApp` 启动类后

http测试接口文件：`springboot-orm-mybatis-plus-demo/http/user-demo-http.http`

## 测试用例接口：
```http request

### 保存用户信息
### 保存用户信息
POST http://localhost:8082/demo/user/save
Content-Type: application/json

{
  "name": "yunnuo",
  "age": 23,
  "sex": 1,
  "email": "2552846359@qq.com"
}


### 分页查询(mybatis-plus)用户信息列表
POST http://localhost:8082/demo/user/pageForMp
Content-Type: application/json

{
  "name": "Tom",
  "current":1,
  "size": 2
}


### 分页查询(pagehelper)用户信息列表
POST http://localhost:8082/demo/user/pageForPageHelper
Content-Type: application/json

{
  "name": "Tom",
  "current":1,
  "size": 2
}


### 根据id获取用户信息
GET http://localhost:8082/demo/user/getById/1

### 响应示例
# {
#  "code": 200,
#  "msg": "操作成功",
#  "res": {
#    "id": 1,
#    "name": "yunnuo",
#    "age": 23,
#    "sex": 1,
#    "email": "2552846359@qq.com"
#  }
# }


```
## 文档
PageHelper官方文档: https://pagehelper.github.io/docs/howtouse/
MyBatis-Plus官方文档：https://baomidou.com/
