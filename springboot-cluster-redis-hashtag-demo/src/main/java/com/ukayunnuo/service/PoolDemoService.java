package com.ukayunnuo.service;

import java.util.Set;

/**
 * pool demo 业务接口
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-05-22
 */
public interface PoolDemoService {
    /**
     * 获取数据池中的数据
     * @param uid 用户id
     * @return 数据池数据
     */
    Set<String> getPoolData(Long uid);
}
