package com.ukayunnuo.constants;

/**
 * RabbitMQ常量池
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-18
 */
public class RabbitConstants {


    /**
     * 路由key 常量池
     *
     * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
     * @date 2023-09-18
     */
    public static class RoutingKey {

        /**
         * DIRECT 路由测试
         */
        public static final String DIRECT_ROUTING_KEY_DEMO = "direct.routing.demo";

        /**
         * TOPIC 路由测试 匹配后面某一个
         */
        public static final String TOPIC_ROUTING_KEY_DEMO_SINGLE = "topic.queue.demo.#";
        /**
         * TOPIC 路由测试 匹配后面所有
         */
        public static final String TOPIC_ROUTING_KEY_DEMO_ALL = "topic.queue.*";

        /**
         * 延迟队列 routingKey
         */
        public static final String DELAY_ROUTING_KEY_DEMO = "delay.routing.demo";

    }


    /**
     * Exchange 常量池
     *
     * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
     * @date 2023-09-18
     */
    public static class Exchange {

        /**
         * 直连模式测试
         */
        public static final String DIRECT_MODE_EXCHANGE_DEMO = "direct.exchange.demo";

        /**
         * 分列模式
         */
        public static final String FANOUT_MODE_EXCHANGE_DEMO = "fanout.exchange.demo";

        /**
         * 主题模式测试
         */
        public static final String TOPIC_MODE_EXCHANGE_DEMO = "topic.exchange.demo";

        /**
         * 延迟队列测试
         */
        public static final String DELAY_MODE_EXCHANGE_DEMO = "delay.exchange.demo";

    }

    /**
     * Queue 常量池
     *
     * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
     * @date 2023-09-18
     */
    public static class Queue {

        /**
         * 直连模式测试
         */
        public static final String DIRECT_MODE_QUEUE_DEMO = "direct.queue.demo";


        /**
         * 路由模式测试
         */
        public static final String ROUTING_MODE_QUEUE_DEMO_1 = "routing.queue.demo1";
        public static final String ROUTING_MODE_QUEUE_DEMO_2 = "routing.queue.demo2";

        /**
         * 广播模式测试
         */
        public static final String FANOUT_MODE_QUEUE_DEMO_1 = "fanout.queue.demo1";
        public static final String FANOUT_MODE_QUEUE_DEMO_2 = "fanout.queue.demo2";

        /**
         * 主题模式测试
         */
        public static final String TOPIC_MODE_QUEUE_DEMO_1 = "topic.queue.demo1";
        public static final String TOPIC_MODE_QUEUE_DEMO_2 = "topic.queue.demo2";

        /**
         * 延迟队列测试 注意：需要开启RabbitMQ 的插件 "rabbitmq_delayed_message_exchange"
         */
        public static final String DELAY_MODE_QUEUE_DEMO = "delay.queue.demo";
    }
}
