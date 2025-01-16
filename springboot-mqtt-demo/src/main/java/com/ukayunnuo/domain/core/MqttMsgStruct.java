package com.ukayunnuo.domain.core;

import com.ukayunnuo.enums.MqttQos;
import lombok.Data;

/**
 * @author yunnuo
 * @since 1.0.0
 */
@Data
public class MqttMsgStruct {

    private String topic;

    private MqttQos qos;

    private String message;


}
