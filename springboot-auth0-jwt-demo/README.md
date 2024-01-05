# 该项目介绍了SpringBoot如何集成auth0-jwt插件,实现token校验功能

## pom依赖

```xml

    <properties>
            <java.version>1.8</java.version>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <lombok.version>1.18.28</lombok.version>
            <fastjson2.version>2.0.34</fastjson2.version>
            <hutool.version>5.8.21</hutool.version>
            <jwt.version>4.4.0</jwt.version>
            <redis.version>1.3.1.RELEASE</redis.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- jwt -->
        <dependency>
            <groupId>com.auth0</groupId>
            <artifactId>java-jwt</artifactId>
            <version>${jwt.version}</version>
        </dependency>

        <!-- redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-redis</artifactId>
            <version>${redis.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
        </dependency>

        <!-- fastjson2 -->
        <dependency>
            <groupId>com.alibaba.fastjson2</groupId>
            <artifactId>fastjson2</artifactId>
            <version>${fastjson2.version}</version>
        </dependency>

        <!--    hutool工具类    -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>${hutool.version}</version>
        </dependency>

    </dependencies>

```

## JWT配置
### yaml
```yaml

# jwt 配置
jwt:
  # 加密key
  key: "ukayunnuo"
  # 认证header头 字段
  authentication: "Authorization"
  # 过期时间 30 分钟 (单位：秒)
  expire-at: 1800
  # token 白名单 url
  white-token-uri:
    - /auth0/jwt/demo/whiteTokenTest

# redis配置
spring:
  #redis配置 单机版本
  redis:
    # 地址
    host: ${SPRINGBOOT_DEMO_REDIS_IP:127.0.0.1}
    # 端口，默认：6379
    port: ${SPRINGBOOT_DEMO_REDIS_PORT:6379}
    # 密码
    password: ${SPRINGBOOT_DEMO_REDIS_PWD:}
    # 数据库
    database: ${SPRINGBOOT_DEMO_REDIS_DATABASE:0}
    # 连接超时时间
    timeout: 1000
    lettuce:
      pool:
        min-idle: 0
        max-idle: 8
        max-active: 20
        max-wait: -1ms
    jedis:
      pool:
        max-active: 200
        max-wait: -1ms
        max-idle: 10
        min-idle: 2


```

## 读取yaml配置
```java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * JWT 配置类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-03
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * 加密 key
     */
    private String key = "ukayunnuo";

    /**
     * 过期时间 默认 30 分钟 (单位秒)
     */
    private Integer expireAt = 30 * 60;

    /**
     * 认证 header头的对应认证token字段
     */
    private String authentication = "Authorization";

    /**
     * token 白名单 url
     */
    private List<String> whiteTokenUri;

}

```

## JWT Util 工具类
```java

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
            Claim expCla = claimMap.get("exp");

            if (expCla == null){
                return false;
            }

            long expAt = expCla.as(Long.class) * 1000L;
            if (System.currentTimeMillis() > expAt) {
                return false;
            }

            Claim uidCla = claimMap.get("iss");
            if (uidCla == null || uidCla.asString().isEmpty()) {
                return false;
            }
            String redisKey = RedisKey.JWT_TOKEN.makeRedisKey(uidCla.asString());

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

```

### jwt 拦截器
````java

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
 * JWT 拦截器
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

````

## WebMvc注册拦截器
```java

/**
 * WebMvc 拦截器控制 配置
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-04
 */
@SpringBootConfiguration
public class WebMvcConfig implements WebMvcConfigurer{

    @Bean
    public JwtInterceptor jwtInterceptor(){
       return new JwtInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**");
    }
}


```
