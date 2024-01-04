package com.ukayunnuo.domain.request;

import lombok.Data;

/**
 * 登录请求
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-04
 */
@Data
public class LoginReq {

    private Long uid;

    private String userName;

}
