package com.ukayunnuo.domain.request;

import lombok.Data;

/**
 * kafka 发送 请求
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-12-27
 */
@Data
public class KafkaPushDemoReq {

    private String topic;

    private String msg;

}
