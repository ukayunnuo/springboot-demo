package com.ukayunnuo.interceptor;

import cn.hutool.core.util.StrUtil;
import com.ukayunnuo.utils.JwtUtil;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 过滤器
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-04
 */
public class JwtInterceptor implements HandlerInterceptor {


    private static final List<String> WHITE_TOKEN_URI_LIST = Arrays.asList("/demo");

    private final AntPathMatcher matcher = new AntPathMatcher();


    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestUri = request.getRequestURI();

        if (isMatchUri(requestUri, WHITE_TOKEN_URI_LIST)){
            return true;
        }

        // 从请求头中获取token
        String token = jwtUtil.getToken(request);

        // 验证token，如果无效或过期，返回false，请求被中断
        if (token == null || Boolean.FALSE.equals(jwtUtil.isValidToken(token))) {
            return false;
        }

        // 如果token有效，返回true，请求继续处理
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在请求处理后执行的操作
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
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
