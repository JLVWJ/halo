package com.lvwj.halo.core.snowflake;

import com.lvwj.halo.common.constants.SystemConstant;
import com.lvwj.halo.core.snowflake.workid.WorkIdGenerator;
import com.lvwj.halo.core.snowflake.workid.ZkWorkIdGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 雪花算法配置
 *
 * @author lvweijie
 * @date 2022/11/15 7:32 PM
 */
@AutoConfiguration
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeConfiguration {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "halo.snowflake.enabled", havingValue = "true", matchIfMissing = true)
  public SnowflakeGenerator snowflakeGenerator(SnowflakeProperties properties) {
    WorkIdGenerator idGenerator = new ZkWorkIdGenerator(properties.getNodeName(), properties.getZookeeperUrl(), properties.getWorkerIdBits());
    SystemConstant.MACHINE_ID = idGenerator.getWorkId();
    return new SnowflakeGenerator(properties, SystemConstant.MACHINE_ID);
  }
}
