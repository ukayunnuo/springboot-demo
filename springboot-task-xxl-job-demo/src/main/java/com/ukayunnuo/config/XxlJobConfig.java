package com.ukayunnuo.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job 配置类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(XxlJobProps.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class XxlJobConfig {

    private final XxlJobProps xxlJobProps;

//    @Bean(initMethod = "start", destroyMethod = "destroy") // 低版本(2.2.0以下)使用
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init start.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProps.getAdmin().getAddress());
        xxlJobSpringExecutor.setAccessToken(xxlJobProps.getAccessToken());
        xxlJobSpringExecutor.setAppname(xxlJobProps.getExecutor().getAppName());
        xxlJobSpringExecutor.setIp(xxlJobProps.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(xxlJobProps.getExecutor().getPort());
        xxlJobSpringExecutor.setLogPath(xxlJobProps.getExecutor().getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProps.getExecutor().getLogRetentionDays());

        log.info(">>>>>>>>>>> xxl-job config init info :{}", xxlJobProps);
        return xxlJobSpringExecutor;
    }

}
