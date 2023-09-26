package com.ukayunnuo.enums;

/**
 * 监听key 状态枚举
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-26
 */
public enum WatchKeyStatus {

    /**
     * 监听成功
     */
    SUCCEEDED,

    /**
     * 监听失败
     */
    FAILED,

    /**
     * 无需再次监听
     */
    NO_NEED_MONITOR,

    /**
     * 不监听
     */
    NO_MONITOR,

    /**
     * 走缓存，但是没有进行监听
     */
    CACHE_NO_MONITOR,
    ;
}
