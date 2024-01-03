# 该示例为SpringBoot集成 Websocket 实现服务与客户端进行消息发送和接收
## 介绍
WebSocket是一种在单个TCP连接上进行全双工通信的协议。WebSocket使得客户端和服务器之间的数据交换变得更加简单，允许服务端主动向客户端推送数据。

## 效果

### 客户端效果![客户端效果图](https://img-blog.csdnimg.cn/direct/a3351a0848d74d3b91124ea687b3b501.png)

### 服务端日志
![服务端日志](https://img-blog.csdnimg.cn/direct/facb273c630d426085954ab9d69296cb.png)

## pom依赖
```xml

<!-- websocket -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
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
## 配置类
```java

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * websocket 配置类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-12-29
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注入该bean 自动注册使用 @ServerEndpoint注解声明的Websocket endpoint
     *
     * @return {@link ServerEndpointExporter} bean
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册端点
        registry.addEndpoint("/ws/msg")
                //解决跨域问题
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //定义客户端订阅地址的前缀信息
        registry.enableSimpleBroker("/topic");
    }

}

```

## 示例

### 定义端点
```java

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * websocket 测试 endpoint
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-12-29
 */
@Slf4j
@Component
@ServerEndpoint(value = "/topic/ws/demo/{uid}")
public class WebSocketDemoEndpoint {

    /**
     * 连接数量池
     */
    private static final Map<Long, Session> SESSION_POOL = new HashMap<>();


    /**
     * 开始连接
     *
     * @param session session
     * @param uid     用户标识
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "uid") Long uid) {
        if (Objects.isNull(uid)) {
            log.warn("onOpen uid is null session:{}", JSONObject.toJSONString(session));
            return;
        }

        SESSION_POOL.put(uid, session);
        log.info("onOpen uid:{}, sessionPool size:{}", uid, SESSION_POOL.size());

    }


    /**
     * 关闭连接
     *
     * @param session session
     * @param uid     用户标识
     */
    @OnClose
    public void onClose(Session session, @PathParam(value = "uid") Long uid) {
        if (Objects.isNull(uid)) {
            log.warn("onClose uid is null session:{}", JSONObject.toJSONString(session));
            return;
        }

        try {
            Session removeSession = SESSION_POOL.remove(uid);
            if (session != null) {
            	session.close();
            }
            if (removeSession!= null && removeSession.isOpen()) {
                removeSession.close();
            }
            log.info("onClose uid:{}, sessionPool size:{}", uid, SESSION_POOL.size());
        } catch (IOException e) {
            log.error("onClose error! uid:{}, e:{}", uid, e.getMessage(), e);
        }

    }

    /**
     * 收到客户端消息
     *
     * @param session session
     * @param uid     用户标识
     * @param message 消息内容
     */
    @OnMessage
    public void onMessage(Session session, @PathParam(value = "uid") Long uid, String message) {
        log.info("onMessage uid:{}, msg:{}", uid, message);
    }


    /**
     * 发送错误
     *
     * @param session session
     * @param uid     uid
     * @param e       异常e
     */
    @OnError
    public void onError(Session session, @PathParam(value = "uid") Long uid, Throwable e) {
        log.error("onError uid:{}, e:{}", uid, e.getMessage(), e);
    }


    /**
     * 广播消息 (SESSION_POOL 池中)
     *
     * @param message message
     */
    public void sendAllMessage(String message) {
        log.info("sendAllMessage:{}", message);

        if (SESSION_POOL.isEmpty()) {
            return;
        }

        SESSION_POOL.forEach((uid, session) -> {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
                log.info("sendAllMessage for uid:{}, message:{}", uid, message);
            }
        });

    }

    /**
     * 多个消息发送
     *
     * @param uids    用户标识
     * @param message 消息
     */
    public void sendMessageToUsers(Set<Long> uids, String message) {
        log.info("sendMessageToUsers uids:{}, msg:{}", JSONObject.toJSONString(uids), message);

        uids.forEach(uid -> sendMessageToUser(uid, message));
    }


    /**
     * 单个消息发送
     *
     * @param uid     用户标识
     * @param message 消息
     */
    public void sendMessageToUser(Long uid, String message) {
        Session session = SESSION_POOL.get(uid);

        if (session != null && session.isOpen()) {
            log.info("sendMessageToUser uid:{}, msg:{}", uid, message);
            session.getAsyncRemote().sendText(message);
        }

    }

}

```

### 客户端（连接、关闭、发送/接收消息）
```html

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>websocket测试</title>
    <link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css">
    <script src="https://cdn.jsdelivr.net/npm/vue"></script>
</head>
<body>
<div id="app">
    <el-container>
        <el-header>
            <el-button @click="connect" type="success">连接</el-button>
            <el-button @click="close" type="danger">断开连接</el-button>
        </el-header>
        <el-main>
            <el-row>
                <el-col :span="6">
                    <el-input
                            type="textarea"
                            :rows="2"
                            placeholder="请输入内容"
                            v-model="textarea">
                    </el-input>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="6">
                    <el-button @click="sendMsg" type="primary" plain>提交消息</el-button>
                </el-col>
            </el-row>

        </el-main>
    </el-container>
</div>
</body>
<!-- import Vue before Element -->
<script src="https://unpkg.com/vue@2/dist/vue.js"></script>
<!-- import JavaScript -->
<script src="https://unpkg.com/element-ui/lib/index.js"></script>


<script>

    const ws_url = "ws://localhost:8088/topic/ws/demo/1";

    new Vue({
        el: '#app',
        data: function () {
            return {
                socket: {},
                textarea: '',
            }
        },

        methods: {

            connect() {
                this.socket = new WebSocket(ws_url);

                this.socket.onopen = function () {
                    ELEMENT.Message({
                        message: '连接服务器成功...',
                        type: 'success'
                    });
                };

                this.socket.onmessage = function (event) {
                    ELEMENT.Message({
                        message: '接收服务器消息: ' + event.data,
                        type: 'success'
                    });
                };

                this.socket.onclose = function () {
                    this.socket = null;
                    ELEMENT.Message({
                        message: '连接服务器断开',
                        type: 'warning'
                    });
                };

                this.socket.onerror = function () {
                    ELEMENT.Message.error('连接服务器失败!');
                    this.socket = null;
                };

            },

            close() {
                this.socket.close();
            },

            sendMsg() {
                if (this.socket != null) {
                    this.socket.send(this.textarea);
                }
            }


        }
    })
</script>
</html>

```

### 服务端发送消息

#### 定义请求DTO
```java

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * 测试
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-02
 */
@Data
public class SendMsgReq {

    private Long uid;

    private String msg;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

```
#### 定义API接口

```java
import com.ukayunnuo.domain.request.SendMsgReq;
import com.ukayunnuo.endpoints.WebSocketDemoEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 发送消息测试
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-02
 */
@Slf4j
@RestController
@RequestMapping("/websocket/demo/sendMsg")
public class SendMsgController {

    @Resource
    private WebSocketDemoEndpoint webSocketDemoEndpoint;

    /**
     * 发送消息测试
     *
     * @param req {@link SendMsgReq} 请求dto
     * @return 发送结果
     */
    @PostMapping
    public void sendMsg(@RequestBody SendMsgReq req) {
        try {
            webSocketDemoEndpoint.sendMessageToUser(req.getUid(), req.getMsg());
        } catch (Exception e) {
            log.error("send msg error! req:{}, e:{}", req, e.getMessage(), e);
        }
    }
}
```
### http请求

```http request
### 发送消息请求
POST http://localhost:8088/websocket/demo/sendMsg
Content-Type: application/json

{
  "uid": 1,
  "msg": "测试"

}
```
