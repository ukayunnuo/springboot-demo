package com.ukayunnuo.es.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * 通用返回请求类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回状态码
     */
    public Integer code;

    /**
     * 返回内容
     */
    public String msg;

    /**
     * 响应数据对象
     */
    public T res;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date serverTime;
    
    private long serverTimestamp;



    /**
     * 初始化一个新创建的 Result 对象，使其表示一个空消息。
     */
    public Result() {
        this(HttpStatus.OK.value(), null, null);
    }

    /**
     * 初始化一个新创建的 Result 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public Result(Integer code, String msg) {
        this(code, msg, null);
    }

    /**
     * 初始化一个新创建的 Result 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param res  数据对象
     */
    public Result(Integer code, String msg, T res) {
        this.code = code;
        this.msg = msg;
        this.res = res;
        this.serverTime = new Date();
        this.serverTimestamp = System.currentTimeMillis();
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static <T> Result<T> success() {
        return Result.success("Successful operation!");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <T> Result<T> success(T res) {
        return Result.success("Successful operation!", res);
    }


    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static <T> Result<T> success(String msg) {
        return Result.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param res 数据对象
     * @return 成功消息
     */
    public static <T> Result<T> success(String msg, T res) {
        return new Result<>(HttpStatus.OK.value(), msg, res);
    }

    /**
     * 返回错误消息
     *
     * @return T
     */
    public static <T> Result<T> error() {
        return Result.error("Operation failure!");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> Result<T> error(String msg) {
        return Result.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param res 数据对象
     * @return 警告消息
     */
    public static <T> Result<T> error(String msg, T res) {
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, res);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> error(HttpStatus httpStatus) {
        return new Result<>(httpStatus.value(), httpStatus.getReasonPhrase(), null);
    }

}
