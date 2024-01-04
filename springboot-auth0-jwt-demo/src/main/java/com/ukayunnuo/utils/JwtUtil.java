package com.ukayunnuo.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.ukayunnuo.config.JwtConfig;
import com.ukayunnuo.core.RedisKey;
import com.ukayunnuo.core.TokenData;
import com.ukayunnuo.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT 工具类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-03
 */
@EnableConfigurationProperties(JwtConfig.class)
@Configuration
@Slf4j
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


    public String createJwtToken(Long uid, String userName, Integer expiration) {
        Date now = new Date();

        JWTCreator.Builder jwtBuilder = JWT.create()
                .withJWTId(String.valueOf(uid))
                // 发行者
                .withIssuer(String.valueOf(uid))
                // 主题
                .withSubject(userName)
                // 自定义内容
                .withClaim("userName", userName)
                .withClaim("tokenData", BeanUtil.beanToMap(TokenData.builder().uid(uid).userName(userName).build()));


        // 过期时间
        if (jwtConfig.getExpireAt() > 0) {
            jwtBuilder.withExpiresAt(DateUtil.offsetSecond(now, expiration));
        }

        // 签名算法
        String token = jwtBuilder.sign(getAlgorithm());

        // 将生成的token 存储到 Redis 中
        stringRedisTemplate.opsForValue()
                .set(RedisKey.JWT_TOKEN.makeRedisKey(uid), token, RedisKey.JWT_TOKEN.expireTime(), TimeUnit.SECONDS);

        return token;
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtConfig.getKey());
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
        Map<String, Claim> claims = parseToken(token);
        return claims.get("tokenData").as(TokenData.class);
    }

    /**
     * 解析Token
     *
     * @param token token
     * @return 结果
     */
    public Map<String, Claim> parseToken(String token) {


        try {
            if (StrUtil.isBlank(token)) {
                throw new IllegalArgumentException("token must be not empty!");
            }

            return JWT.require(getAlgorithm()).build().verify(token).getClaims();

        } catch (IllegalArgumentException | JWTVerificationException | SecurityException e) {
            log.warn("parseToken failed! token:{}, e:{}", token, e.getMessage());
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "parseToken failed! case:" + e.getMessage());
        } catch (Exception e) {
            log.error("parseToken error! token:{}, e:{}", token, e.getMessage(), e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "parseToken error!");
        }

    }

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

            Map<String, Claim> claimMap = parseToken(token);

            // 验证过期时间
            long expAt = claimMap.get("exp").as(Long.class) * 1000L;
            if (System.currentTimeMillis() > expAt) {
                return false;
            }

            // 验证签名算法
            String algorithm = claimMap.get("alg").asString();
            if (algorithm == null || algorithm.isEmpty() || Boolean.FALSE.equals(algorithm.equals(jwtConfig.getKey()))) {
                return false;
            }

            String redisKey = RedisKey.JWT_TOKEN.makeRedisKey(claimMap.get("id").asString());

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
