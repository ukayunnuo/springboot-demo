package com.ukayunnuo.pattern.factorypattern;

import com.ukayunnuo.pattern.factorypattern.impl.FactoryProduceAImpl;
import com.ukayunnuo.pattern.factorypattern.impl.FactoryProduceBImpl;

/**
 * 工厂模式 - 工厂类
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-10-17
 */

public class ProduceFactory {

    public FactoryProduce create(String type) {
        if ("A".equals(type)) {
            return new FactoryProduceAImpl();
        } else if ("B".equals(type)) {
            return new FactoryProduceBImpl();
        }
        throw new IllegalArgumentException("Invalid product type: " + type);
    }
}

