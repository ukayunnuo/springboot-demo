package com.ukayunnuo.constants;

/**
 * RabbitMQ常量池
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-18
 */
public class RabbitConstants {


    /**
     * topic 路由
     *
     * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
     * @date 2023-09-18
     */
    public static class TopicRoutingKey {

        /**
         * 路由测试
         */
        public static final String ROUTING_KEY_DEMO = "queue.demo.#";

    }


    /**
     * Exchange 常量类
     *
     * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
     * @date 2023-09-18
     */
    public static class ExchangeConstants {

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
     * Queue 常量类
     *
     * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
     * @date 2023-09-18
     */
    public static class QueueConstants {

        /**
         * 直连模式测试
         */
        public static final String DIRECT_MODE_QUEUE_DEMO = "direct.queue.demo";

        /**
         * 分列模式
         */
        public static final String FANOUT_MODE_QUEUE_DEMO = "fanout.queue.demo";

        /**
         * 主题模式测试
         */
        public static final String TOPIC_MODE_QUEUE_DEMO = "topic.queue.demo";

        /**
         * 延迟队列测试
         */
        public static final String DELAY_MODE_QUEUE_DEMO = "delay.queue.demo";
    }
}
