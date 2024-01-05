package com.ukayunnuo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * JWT 配置类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-03
 */
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * 加密 key 需保证32位长度
     */
    private String key = "eXVubnVveXVubnVveXVubnVveXVubnVveXVubnVvMDA=";

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
    private List<String> whiteTokenUri = new ArrayList<>();

}
