package com.ukayunnuo.endpoints;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private static final ConcurrentHashMap<Long, Session> SESSION_POOL = new ConcurrentHashMap<>();


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
            if (removeSession != null && removeSession.isOpen()) {
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
