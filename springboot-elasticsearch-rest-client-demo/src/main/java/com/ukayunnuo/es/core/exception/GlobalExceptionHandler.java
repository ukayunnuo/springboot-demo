package com.ukayunnuo.es.core.exception;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.ukayunnuo.es.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 全局异常处理程序
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public <T> Result<T> handleServiceException(ServiceException e, HttpServletRequest request) {
        Map<String, String[]> reqParams = ServletUtil.getParams(request);
        Integer status = ObjectUtil.defaultIfNull(e.getCode(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        log.warn("handleServiceException Abnormal data request ! path：{}, Request parameter：{}, status:{}, e:{}", request.getRequestURI(), reqParams, e.getCode(), e.getMessage(), e);
        return Result.error(status, e.getMessage());
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public <T> Result<T> handleHttpMessageNotReadableException(HttpMessageNotReadableException e,HttpServletRequest request){
        Map<String, String[]> reqParams = ServletUtil.getParams(request);
        log.warn("handleHttpMessageNotReadableException Abnormal data request ! path：{}, Request parameter：{}, e:{}", request.getRequestURI(), reqParams, e.getMessage(), e);
        return Result.error(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public <T> Result<T> handleException(Exception e,HttpServletRequest request){
        Map<String, String[]> reqParams = ServletUtil.getParams(request);
        log.error("handleException System error ! path：{}, Request parameter：{}, e:{}", request.getRequestURI(), reqParams, e.getMessage(), e);
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
