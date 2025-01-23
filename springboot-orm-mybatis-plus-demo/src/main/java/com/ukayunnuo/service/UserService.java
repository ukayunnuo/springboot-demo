package com.ukayunnuo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ukayunnuo.domain.entity.User;
import com.ukayunnuo.domain.request.UserPageReq;

/**
 * User 服务接口
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @since 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 保存userinfo
     *
     * @param user 用户信息
     * @return {@link Long} 用户 id
     */
    Long saveUserInfo(User user);

    PageInfo<User> getPage(UserPageReq req);
}
