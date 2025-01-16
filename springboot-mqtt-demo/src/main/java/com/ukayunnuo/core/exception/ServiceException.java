package com.ukayunnuo.core.exception;

import com.ukayunnuo.core.enums.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * 业务服务 异常类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-13
 */
public class ServiceException extends RuntimeException {

    private HttpStatus httpStatus;

    private int code;


    public ServiceException(int code, String msg) {
        super(msg);
        this.setCode(code);
    }

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.msg);
        this.setCode(errorCode.code);
    }


    public ServiceException(HttpStatus httpStatus) {
        super(httpStatus.getReasonPhrase());
        this.setCode(httpStatus.value());
        this.setHttpStatus(httpStatus);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
