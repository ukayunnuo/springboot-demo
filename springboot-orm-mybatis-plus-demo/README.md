# springboot-orm-mybatis-plus-demo

#### 介绍

本模块利用springBoot 集成了以下功能插件：

- MyBaits plus 插件
- fastjson2
- lombok
- hutool工具类
- HikariCP
- p6spy
- dynamic 动态数据库插件

#### 开始教程

##### 1. 创建数据库和User表

SQL文件位置：`src/main/resources/sql/user.sql`

##### 2. 修改配置文件连接数据库参数

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

##### 3. 启动和测试

启动：`DatabaseDemoApp` 启动类后

http测试接口文件：`springboot-orm-mybatis-plus-demo/http/user-demo-http.http`

测试用例接口：
```http request

### 保存用户信息
POST http://localhost:8082/demo/user/save
Content-Type: application/json

{
  "name": "yunnuo",
  "age": 23,
  "sex": 1,
  "email": "2552846359@qq.com"
}


### 分页查询用户信息列表
POST http://localhost:8082/demo/user/page
Content-Type: application/json

{
  "name": "yunnuo"
}


### 根据id获取用户信息
GET http://localhost:8082/demo/user/getById/1


```
