package com.lvwj.halo.xxljob.config;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author lvweijie
 * @date 2024年07月12日 13:52
 */
@AutoConfiguration
public class XxlJobConfigration {

    @Value("${halo.xxlJob.admin.addresses}")
    private String adminAddresses;

    @Value("${halo.xxlJob.accessToken}")
    private String accessToken;

    @Value("${halo.xxlJob.executor.appname}")
    private String appname;

    @Value("${halo.xxlJob.executor.address}")
    private String address;

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
        XxlJobHelper.log(">>>>>>>>>>> xxl-job config init.>>>>>>>>>>>");
        System.out.println("=============== xxl-job config init.===============");

        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appname);
        xxlJobSpringExecutor.setAddress(address);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return xxlJobSpringExecutor;
    }
}
