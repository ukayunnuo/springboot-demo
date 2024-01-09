package com.ukayunnuo.core.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.core.Result;
import com.ukayunnuo.core.TokenData;
import com.ukayunnuo.domain.model.LoginUser;
import com.ukayunnuo.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * token认证过滤器
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-05
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // 从请求头中获取token
        String token = jwtUtil.getToken(request);

        if (StrUtil.isNotBlank(token)) {
            TokenData tokenData = jwtUtil.getTokenData(token);
            if (Objects.nonNull(tokenData)) {
                if (Boolean.FALSE.equals(jwtUtil.isValidToken(token))) {
                    log.warn("JwtAuthenticationTokenFilter The verification token failure! token:{}", token);

                    response.setCharacterEncoding("UTF-8");
                    ServletUtil.write(response,
                            JSONObject.toJSONString(Result.error(HttpStatus.BAD_REQUEST.value(), "The verification token failure!")),
                            MediaType.APPLICATION_JSON_VALUE);
                }

                LoginUser loginUser = new LoginUser(tokenData.getUid(), tokenData.getUserName());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, token, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }

        chain.doFilter(request, response);
    }


}
