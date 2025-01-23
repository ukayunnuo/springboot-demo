package com.ukayunnuo.core.enums;

/**
 * @author hxt <a href="xthe3257@cggc.cn">Email: xthe3257@cggc.cn </a>
 * @since 1.0.0
 */
public enum ErrorCode {

    SUCCESS(0, "成功"),


    ERROR_PARAM(400, "参数错误"),

    ERROR_SERVER(500, "服务器错误"),

    ERROR_NOT_FOUND(404, "找不到资源"),

    ERROR_UNAUTHORIZED(401, "未授权"),

    ERROR_FORBIDDEN(403, "禁止访问"),

    SYSTEM_ERROR(10000, "系统错误"),



    ;
    public final int code;
    public final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
