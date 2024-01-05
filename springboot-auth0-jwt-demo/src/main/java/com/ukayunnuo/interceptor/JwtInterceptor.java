package com.ukayunnuo.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.config.JwtConfig;
import com.ukayunnuo.core.Result;
import com.ukayunnuo.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 过滤器
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-04
 */
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private JwtConfig jwtConfig;

    private final AntPathMatcher matcher = new AntPathMatcher();



    private List<String> getWhiteTokenUrlArray() {
        List<String> whiteTokenUrlList =new ArrayList<>(Arrays.asList(
                "/auth0/jwt/demo/login"
        ));

        List<String> whiteTokenUri = jwtConfig.getWhiteTokenUri();

        if (CollUtil.isNotEmpty(whiteTokenUri)){
            whiteTokenUrlList.addAll(whiteTokenUri);
        }

        return whiteTokenUrlList;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestUri = request.getRequestURI();

        if (isMatchUri(requestUri, getWhiteTokenUrlArray())){
            return true;
        }

        // 从请求头中获取token
        String token = jwtUtil.getToken(request);

        // 验证token，如果无效或过期，返回false，请求被中断
        if (token == null || Boolean.FALSE.equals(jwtUtil.isValidToken(token))) {
            log.warn("The verification token failure! token:{}", token);
            ServletUtil.write(response,
                    JSONObject.toJSONString(Result.error(HttpStatus.BAD_REQUEST.value(), "The verification token failure!")),
                    "application/json");

            return false;
        }

        // 如果token有效，返回true，请求继续处理
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 在请求处理后执行的操作
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 在请求完成后的操作
    }


    protected boolean isMatchUri(String path, List<String> uris) {
        if (StrUtil.isBlank(path)) {
            return false;
        }
        for (String pattern : uris) {
            if (match(path, pattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean match(String path, String pattern) {
        if (StrUtil.isBlank(path) || StrUtil.isBlank(pattern)) {
            return false;
        }
        return matcher.match(pattern, path);
    }


}
