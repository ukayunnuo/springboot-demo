package com.ukayunnuo.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ukayunnuo.core.Result;
import com.ukayunnuo.domain.entity.User;
import com.ukayunnuo.domain.request.UserPageReq;
import com.ukayunnuo.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * User 测试 API接口
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-13
 */
@RestController
@RequestMapping("/demo/user")
public class UserDemoController {

    private final UserService userService;

    public UserDemoController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/page")
    public Result<Page<User>> selectAll(@RequestBody UserPageReq req) {

        return Result.success(userService.page(Page.of(req.getCurrent(), req.getSize()), new QueryWrapper<>(BeanUtil.toBean(req, User.class))));
    }

    @GetMapping("/getById/{id}")
    public Result<User> getUserInfoById(@PathVariable Long id){
       return Result.success(userService.getById(id));
    }

    @PostMapping("/save")
    public Result<Long> saveUserInfo(@RequestBody User user){
        return Result.success(userService.saveUserInfo(user));
    }


}
