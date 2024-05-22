package com.ukayunnuo.core;

import lombok.Getter;

/**
 *
 *
 * @author yunnuo
 * @date 2024-05-22
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "成功"),

    FAIL(1, "失败"),

    UNKNOWN_ERROR(2, "未知错误"),

    PARAM_ERROR(3, "参数错误"),

    REQUEST_ERROR(4, "请求错误"),

    REQUEST_TIMEOUT(5, "请求超时"),

    REQUEST_LIMIT(6, "请求频率限制"),

    REQUEST_FORBIDDEN(7, "请求被禁止"),

    REQUEST_NOT_FOUND(8, "请求未找到"),

    SYSTEM_ERROR(9, "系统错误"),

    ;

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }



}
