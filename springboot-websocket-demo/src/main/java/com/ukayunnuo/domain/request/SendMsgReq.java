package com.ukayunnuo.domain.request;

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
