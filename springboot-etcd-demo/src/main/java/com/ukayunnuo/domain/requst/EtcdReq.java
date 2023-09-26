package com.ukayunnuo.domain.requst;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * ETCD test req
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-26
 */
@Data
public class EtcdReq {

    private String key;

    private String value;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
