package com.ukayunnuo.core.processor;

import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证失败处理
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-05
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        log.warn("AuthenticationEntryPoint Authentication failure! url:{}, param:{}, errorMsg:{}", request.getRequestURI(), JSONObject.toJSONString(request.getParameterMap()), e.getMessage());
        response.setCharacterEncoding("UTF-8");
        ServletUtil.write(response,
                JSONObject.toJSONString(Result.error(HttpStatus.UNAUTHORIZED.value(), "Authentication failure, Unable to access system resources!")),
                MediaType.APPLICATION_JSON_VALUE);
    }
}
