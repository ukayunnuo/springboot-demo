package com.ukayunnuo.task;

import com.ukayunnuo.core.Result;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 测试 xxl-job 任务
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-09-26
 */
@Slf4j
@Component
public class XxlJobTaskDemo {

    @XxlJob("demoJobHandler")
    public Result<String> demoJobHandler(String param) {
        log.info("xxl-job demo task execute start! param:{}", param);
        return Result.success("xxl-job demo task execute success");
    }

}
