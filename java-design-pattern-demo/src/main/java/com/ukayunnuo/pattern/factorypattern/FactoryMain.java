package com.ukayunnuo.pattern.factorypattern;

/**
 * 工厂模式-使用测试
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-10-17
 */
public class FactoryMain {
    public static void main(String[] args) {
        ProduceFactory produceFactory = new ProduceFactory();
        // 创建 A
        produceFactory.create("A").todo();
        // 创建 B
        produceFactory.create("B").todo();
    }

}
