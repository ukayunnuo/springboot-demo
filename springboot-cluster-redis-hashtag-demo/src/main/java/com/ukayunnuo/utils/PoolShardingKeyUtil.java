package com.ukayunnuo.utils;

/**
 *
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-05-22
 */
public class PoolShardingKeyUtil {

    public static String getHashtagPoolShardingKey(String poolShardingKey, String hashtag){
        return String.format("%s:%s", poolShardingKey, hashtag);
    }

}
