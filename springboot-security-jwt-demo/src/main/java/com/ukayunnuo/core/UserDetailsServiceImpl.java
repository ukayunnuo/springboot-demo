package com.ukayunnuo.core;


import com.ukayunnuo.domain.model.LoginUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * user 信息 验证处理
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-04
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // todo: 通过用户唯一表示查询用户数据, 此处省略....
        String password = new BCryptPasswordEncoder().encode("123");
        return new LoginUser(Long.parseLong(username), "yunnuo", password);

    }

}
