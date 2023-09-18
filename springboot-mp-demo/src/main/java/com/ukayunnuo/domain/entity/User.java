package com.ukayunnuo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User 实体类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends Model<User> {


    /**
     * 主键Id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;


    /**
     * 姓名
     */
    private String name;


    /**
     * 年龄
     */
    private Integer age;


    /**
     * 性别
     */
    private Integer sex;


    /**
     * 邮箱
     */
    private String email;

}

