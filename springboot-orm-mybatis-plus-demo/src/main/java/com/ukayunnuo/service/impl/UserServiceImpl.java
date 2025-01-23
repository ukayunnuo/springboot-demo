package com.ukayunnuo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ukayunnuo.core.exception.ServiceException;
import com.ukayunnuo.domain.entity.User;
import com.ukayunnuo.domain.request.UserPageReq;
import com.ukayunnuo.mapper.UserMapper;
import com.ukayunnuo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * User 服务实现
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @since 1.0.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Override
    public Long saveUserInfo(User user) {

        if (Objects.isNull(user)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST);
        }

        if (Objects.nonNull(user.getId())) {
            user.setId(null);
        }
        boolean flag = this.save(user);

        if (flag) {
            return user.getId();
        }
        throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "user info save failed!");

    }

    @Override
    public PageInfo<User> getPage(UserPageReq req) {

        try (Page<User> userPage = PageHelper.startPage(Math.toIntExact(req.getCurrent()), Math.toIntExact(req.getSize()))) {
            return userPage.doSelectPageInfo(() -> this.list(new QueryWrapper<>(BeanUtil.copyProperties(req, User.class))));
        }

    }
}
