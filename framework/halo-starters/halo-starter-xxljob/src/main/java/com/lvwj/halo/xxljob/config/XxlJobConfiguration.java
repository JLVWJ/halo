package com.lvwj.halo.xxljob.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author lvweijie
 * @date 2024年07月12日 13:52
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = "halo.xxlJob", value = "enabled", havingValue = "true")
public class XxlJobConfiguration {

    @Value("${halo.xxlJob.admin.addresses}")
    private String adminAddresses;

    @Value("${halo.xxlJob.accessToken}")
    private String accessToken;

    @Value("${halo.xxlJob.executor.appname}")
    private String appname;

    @Value("${halo.xxlJob.executor.ip}")
    private String ip;

    @Value("${halo.xxlJob.executor.port}")
    private int port;

    @Value("${halo.xxlJob.executor.logPath}")
    private String logPath;

    @Value("${halo.xxlJob.executor.logRetentionDays}")
    private int logRetentionDays;


    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init start>>>>>>>>>>>");

        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(this.adminAddresses);
        xxlJobSpringExecutor.setAppName(this.appname);
        xxlJobSpringExecutor.setIp(this.ip);
        xxlJobSpringExecutor.setPort(this.port);
        xxlJobSpringExecutor.setAccessToken(this.accessToken);
        xxlJobSpringExecutor.setLogPath(this.logPath);
        xxlJobSpringExecutor.setLogRetentionDays(this.logRetentionDays);

        log.info(">>>>>>>>>>> xxl-job config init success.>>>>>>>>>>>");
        return xxlJobSpringExecutor;
    }
}
