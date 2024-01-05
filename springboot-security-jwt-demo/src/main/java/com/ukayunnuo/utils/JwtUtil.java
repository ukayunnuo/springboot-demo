package com.ukayunnuo.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ukayunnuo.config.JwtConfig;
import com.ukayunnuo.core.RedisKey;
import com.ukayunnuo.core.TokenData;
import com.ukayunnuo.core.exception.ServiceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * jwt 工具类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-05
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(JwtConfig.class)
public class JwtUtil {

    @Resource
    private JwtConfig jwtConfig;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 创建JWT token 字符串
     *
     * @param uid      用户id
     * @param userName 用户名
     * @return token
     */
    public String createJwtToken(Long uid, String userName) {
        return createJwtToken(uid, userName, jwtConfig.getExpireAt());
    }

    /**
     * 创建 token
     *
     * @param uid        用户uid
     * @param userName   用户名
     * @param expiration 过期时长(单位：秒）
     * @return token
     */
    public String createJwtToken(Long uid, String userName, Integer expiration) {
        Date now = new Date();
        JwtBuilder jwtBuilder = Jwts.builder()
                .id(uid.toString())
                .subject(userName)
                .issuedAt(now)
                .signWith(getSecretKey())
                .claims(BeanUtil.beanToMap(TokenData.builder().uid(uid).userName(userName).build()));

        if (expiration > 0) {
            jwtBuilder.expiration(DateUtil.offsetSecond(now, expiration));
        }

        String token = jwtBuilder.compact();

        // 将生成的token 存储到 Redis 中
        stringRedisTemplate.opsForValue()
                .set(RedisKey.JWT_TOKEN.makeRedisKey(uid), token, RedisKey.JWT_TOKEN.expireTime(), TimeUnit.SECONDS);

        return token;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getKey().getBytes());
    }

    /**
     * 获取 token
     *
     * @param request 请求
     * @return token
     */
    public String getToken(HttpServletRequest request) {
        return request.getHeader(jwtConfig.getAuthentication());
    }

    /**
     * 获取token信息
     *
     * @param request request
     * @return {@link TokenData} token信息
     */
    public TokenData getTokenData(HttpServletRequest request) {
        return getTokenData(getToken(request));
    }

    /**
     * 获取token信息
     *
     * @param token JWT
     * @return {@link TokenData} token信息
     */
    public TokenData getTokenData(String token) {
        try {

            Claims claims = parseToken(token);
            if (Objects.nonNull(claims)) {
                return claims.get("tokenData", TokenData.class);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 解析Token
     *
     * @param token token
     * @return 结果
     */
    public Claims parseToken(String token) {
        try {

            if (StrUtil.isBlank(token)) {
                throw new IllegalArgumentException("token must be not empty!");
            }

            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (IllegalArgumentException | JwtException e) {
            log.warn("parseToken failed! token:{}, e:{}", token, e.getMessage());
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "parseToken failed! case:" + e.getMessage());
        } catch (Exception e) {
            log.error("parseToken error! token:{}, e:{}", token, e.getMessage(), e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "parseToken error!");
        }

    }

    /**
     * 校验 token
     *
     * @param token token
     * @return 校验结果
     */
    public boolean isValidToken(String token) {

        try {
            if (token == null || token.isEmpty()) {
                return false;
            }

            // JWT 格式为 "header.payload.signature"
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            Claims claims = parseToken(token);

            // 验证过期时间
            Date expiration = claims.getExpiration();

            if (expiration == null) {
                return false;
            }

            if (expiration.before(new Date())) {
                return false;
            }

            String issuer = claims.getIssuer();
            if (issuer == null || issuer.isEmpty()) {
                return false;
            }
            String redisKey = RedisKey.JWT_TOKEN.makeRedisKey(issuer);

            // 校验redis中的JWT是否存在
            if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(redisKey))) {
                throw new SecurityException("token have expired");
            }

            // 防止不同设备登录
            String redisToken = stringRedisTemplate.opsForValue().get(redisKey);
            if (!StrUtil.equals(token, redisToken)) {
                throw new SecurityException("token have expired");
            }

            return true;
        } catch (ServiceException e) {
            log.warn("isValidToken no pass! token:{}, case:{}", token, e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("isValidToken failed! token:{}, e:{}", token, e.getMessage(), e);
            return false;
        }


    }


    /**
     * 移除token
     *
     * @param request 请求
     */
    public void removeToken(HttpServletRequest request) {
        removeToken(getToken(request));
    }

    /**
     * 移除token
     *
     * @param token token
     */
    public void removeToken(String token) {
        TokenData tokenData = getTokenData(token);
        stringRedisTemplate.delete(RedisKey.JWT_TOKEN.makeRedisKey(tokenData.uid));
    }


}
