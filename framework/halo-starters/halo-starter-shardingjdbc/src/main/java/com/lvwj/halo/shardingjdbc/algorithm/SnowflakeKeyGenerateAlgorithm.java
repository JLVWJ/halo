package com.lvwj.halo.shardingjdbc.algorithm;

import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.ThreadLocalUtil;
import com.lvwj.halo.core.snowflake.SnowflakeUtil;
import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;

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

  private Long shardingValue;

  @Override
  public Comparable<?> generateKey() {
    if (null == shardingValue || shardingValue == 0) {
      shardingValue = ThreadLocalUtil.getCurrentUserId();
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
    this.shardingValue = Func.toLong(props.getProperty(SHARDING_VALUE));
  }

  @Override
  public String getType() {
    return "SNOWFLAKE-KEY";
  }
}
