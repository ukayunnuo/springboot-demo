package com.ukayunnuo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ukayunnuo.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * User 数据库访问层
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    Boolean checkUserName(@Param("userName") String userName);

}
