package com.ukayunnuo.domain.request;

import lombok.Data;

/**
 * redis 请求参数
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Data
public class RedisParamReq {

    private String key;

    private String value;

}
