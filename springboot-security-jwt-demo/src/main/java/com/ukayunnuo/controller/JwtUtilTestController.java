package com.ukayunnuo.controller;

import com.ukayunnuo.core.Result;
import com.ukayunnuo.core.TokenData;
import com.ukayunnuo.domain.request.LoginReq;
import com.ukayunnuo.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-04
 */
@Slf4j
@RestController
@RequestMapping("/security/jwt/demo")
public class JwtUtilTestController {

    @Resource
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Resource
    private JwtUtil jwtUtil;

    @PostMapping("/whiteTokenTest")
    public Result<String> whiteTokenDemo(){
        log.info("whiteTokenTest is success!");
        return Result.success("OK");
    }

    @PostMapping("/login")
    public Result<String> getToken(@RequestBody LoginReq loginReq) throws Exception {

        // 调用UserDetailsServiceImpl.loadUserByUsername
        Authentication authenticate = authenticationManagerBuilder.build().authenticate(new UsernamePasswordAuthenticationToken(loginReq.getUserName(), loginReq.getPassword()));

        return Result.success(jwtUtil.createJwtToken(loginReq.getUid(), loginReq.getUserName()));
    }

    @PostMapping("/getUserInfo")
    public Result<TokenData> getUserInfo(@RequestBody LoginReq loginReq, HttpServletRequest request){
        return Result.success(jwtUtil.getTokenData(request));
    }

}
