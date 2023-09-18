package com.ukayunnuo.domain.response;

import lombok.Builder;
import lombok.Data;

/**
 * redis 测试响应结果
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-18
 */
@Builder
@Data
public class RedisRes {

    private String key;

    private Object value;

    private Long ttl;

}
