package com.ukayunnuo;


import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 打印 日志测试
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Component
public class PrintLogTest {


    @PostConstruct
    public void printLogTest(){

            log.info("=============================日志测试打印======================================");

            log.info("日志测试-->INFO");
            log.warn("日志测试-->WARN");
            log.error("日志测试-->ERROR");

            Map<String, Object> map = new HashMap<>(3);
            map.put("name", "yunnuo");
            map.put("age", 23);
            map.put("email", "2552846359@qq.com");
            log.info("author info:{}", JSONObject.toJSONString(map));
    }


}
