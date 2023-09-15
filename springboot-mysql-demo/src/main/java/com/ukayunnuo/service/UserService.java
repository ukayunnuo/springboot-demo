package com.ukayunnuo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ukayunnuo.domain.entity.User;

/**
 * User 服务接口
 *
 * @author yunnuo <a href="nuo.he@backgardon.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-13
 */
public interface UserService extends IService<User> {

    /**
     * 保存userinfo
     *
     * @param user 用户信息
     * @return {@link Long} 用户 id
     */
    Long saveUserInfo(User user);
}