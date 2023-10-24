package com.ukayunnuo.pattern.factorypattern.impl;

import com.ukayunnuo.pattern.factorypattern.FactoryProduce;

/**
 * 工厂模式 - 实现 A
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-10-17
 */
public class FactoryProduceAImpl implements FactoryProduce {
    @Override
    public void todo() {
        System.out.println("A to do something");
    }
}
