package com.ukayunnuo.core.exception;

import org.springframework.http.HttpStatus;

/**
 * 业务服务 异常类
 *
 * @author yunnuo <a href="nuo.he@backgardon.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-13
 */
public class ServiceException extends RuntimeException {

    private HttpStatus httpStatus;

    private int code;


    public ServiceException(int code, String msg) {
        super(msg);
        this.setCode(code);
    }


    public ServiceException(HttpStatus httpStatus) {
        super(httpStatus.getReasonPhrase());
        this.setCode(httpStatus.value());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
