package com.ukayunnuo.domain.request;

import lombok.Data;

/**
 * mq 测试
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-18
 */
@Data
public class MqReq {

    private String queue;

    private String msg;

}
