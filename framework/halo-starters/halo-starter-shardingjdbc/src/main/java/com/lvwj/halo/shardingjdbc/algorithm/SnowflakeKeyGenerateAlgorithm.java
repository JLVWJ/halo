package com.lvwj.halo.shardingjdbc.algorithm;

import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.ThreadLocalUtil;
import com.lvwj.halo.core.snowflake.SnowflakeUtil;
import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;

import java.util.Optional;
import java.util.Properties;

/**
 * 自定义雪花算法key生成算法
 *
 * @author lvwj
 * @date 2023-01-29 11:29
 */
public class SnowflakeKeyGenerateAlgorithm implements KeyGenerateAlgorithm {

  private static final String SHARDING_VALUE = "sharding-value";

  private Properties props = new Properties();

  private Integer shardingValue;

  @Override
  public Comparable<?> generateKey() {
    Long tenantId = ThreadLocalUtil.getTenantId();
    if (null == shardingValue || shardingValue == 0) {
      shardingValue = Optional.ofNullable(tenantId).map(Long::intValue).orElse(null);
    }
    if (null == shardingValue || shardingValue == 0) {
      return SnowflakeUtil.nextId();
    }
    return SnowflakeUtil.nextId(shardingValue);
  }

  @Override
  public Properties getProps() {
    return props;
  }

  @Override
  public void init(Properties properties) {
    this.props = properties;
    this.shardingValue = getShardingValue(properties);
  }

  private Integer getShardingValue(Properties props) {
    String property = props.getProperty(SHARDING_VALUE);
    return Func.isBlank(property) ? null : Func.toInt(property);
  }

  @Override
  public String getType() {
    return "SNOWFLAKE-KEY";
  }
}
