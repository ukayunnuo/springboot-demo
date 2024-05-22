package com.ukayunnuo.controller;

import com.ukayunnuo.core.Result;
import com.ukayunnuo.service.PoolDemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

/**
 *
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-05-22
 */
@RequestMapping("redis/cluster/pool/demo")
@RestController
public class PoolDemoController {

    @Resource
    private PoolDemoService poolDemoService;

    @GetMapping("/getData/{uid}")
    public Result<Set<String>> getPoolData(@PathVariable Long uid) {
        return Result.success(poolDemoService.getPoolData(uid));
    }

}
