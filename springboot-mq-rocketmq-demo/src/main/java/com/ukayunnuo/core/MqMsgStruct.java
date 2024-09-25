package com.ukayunnuo.core;

import com.alibaba.fastjson2.JSONObject;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * MQ 消息体
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-19
 */
@Data
public class MqMsgStruct implements Serializable {

    private String msg;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
