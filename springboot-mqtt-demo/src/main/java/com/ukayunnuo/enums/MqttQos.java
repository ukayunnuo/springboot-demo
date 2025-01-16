package com.ukayunnuo.enums;

/**
 * mqtt qos 枚举
 *
 * @author yunnuo
 * @since 1.0.0
 */
public enum MqttQos {

    /**
     * 最多一次（At most once），消息发送一次，不保证消息到达。 (默认)
     */
    QOS_0(0, "QOS_0"),

    /**
     * 确保消息至少到达一次，但可能会重复。
     */
    QOS_1(1, "QOS_1"),

    /**
     * 确保消息恰好到达一次，适用于对消息传递可靠性要求较高的场景。
     */
    QOS_2(2, "QOS_2");

    public final int code;

    public final String msg;

    MqttQos(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
