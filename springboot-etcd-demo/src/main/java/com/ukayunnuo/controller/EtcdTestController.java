package com.ukayunnuo.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.requst.EtcdReq;
import com.ukayunnuo.enums.WatchKeyStatus;
import com.ukayunnuo.utils.EtcdHandleUtil;
import io.etcd.jetcd.kv.PutResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * 测试类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-26
 */
@Slf4j
@RequestMapping("/etcd/demo")
@RestController
public class EtcdTestController {

    @Resource
    private EtcdHandleUtil etcdHandleUtil;

    @PostMapping("/pushTest")
    public Result<PutResponse> pushTest(@RequestBody EtcdReq req) throws ExecutionException, InterruptedException {
        PutResponse putResponse = etcdHandleUtil.put(req.getKey(), req.getValue()).get();
        WatchKeyStatus watchKeyStatus = etcdHandleUtil.watchKeyHandlerAndCache(req.getKey());
        log.info("pushTest  req:{}, putResponse:{}, watchKeyStatus:{}", req, JSONObject.toJSONString(putResponse), watchKeyStatus);
        return Result.success(putResponse);
    }

    @PostMapping("/get")
    public Result<String> get(@RequestBody EtcdReq req) {
        return Result.success(etcdHandleUtil.get(req.getKey()));
    }

}
