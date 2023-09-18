package com.ukayunnuo.domain.request;

import com.ukayunnuo.domain.entity.User;
import lombok.Data;

/**
 * user page 请求 dto
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-13
 */
@Data
public class UserPageReq extends User {

    private Long size = 10L;

    private Long current  = 1L;


}
