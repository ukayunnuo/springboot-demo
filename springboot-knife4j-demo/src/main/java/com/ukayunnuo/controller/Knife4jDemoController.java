package com.ukayunnuo.controller;

import cn.hutool.core.bean.BeanUtil;
import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.request.Knife4jParamReq;
import com.ukayunnuo.domain.response.Knife4jRes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * knife4j 测试 api 接口
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Api(value = "API 测试接口", tags = {"test"})
@RestController
@RequestMapping("/demo/knife4j")
public class Knife4jDemoController {

    @ApiOperation(value = "api 测试接口", notes = "测试接口用例")
    @PostMapping
    public Result<Knife4jRes> testApi(@RequestBody Knife4jParamReq req) {
        return Result.success(BeanUtil.copyProperties(req, Knife4jRes.class));
    }


}
