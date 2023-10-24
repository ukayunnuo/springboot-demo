package com.ukayunnuo.pattern;

/**
 * 单例模式
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-10-17
 */
public class SingletonPattern {

    private static SingletonPattern instance;

    public SingletonPattern() {
    }

    public static SingletonPattern getInstance() {
        if (instance == null) {
            instance = new SingletonPattern();
        }
        return instance;
    }
}
