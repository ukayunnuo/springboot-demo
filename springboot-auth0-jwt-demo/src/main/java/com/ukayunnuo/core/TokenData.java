package com.ukayunnuo.core;

import lombok.*;

import java.io.Serializable;

/**
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-01-04
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TokenData implements Serializable {

    public Long uid;

    public String userName;

}
