package com.lvwj.halo.core.snowflake;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 雪花ID 配置项目
 *
 * 雪花算法默认位如下：
 *     ------------- 41bits 时间戳-------------------                -12bits seq-
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 0000000000 - 0000000000 00
 * 1bits 符号位                                       -10bits workId-
 *
 * 雪花算法支持对工作ID、序列位 扩展，支持 shardingBits（分片键位数），业务可用于分表定位
 * 即：可支配的位数为 10bits workId + 12bits sequence = 22 bits
 *
 * 约定时间位固定40bits，减少一位
 * 约定单个服务集群节点数至少需要支持200+节点，机器位不能少于 8bits
 * 约定seq位数不能少于：7bits
 *
 * shardingBits最大支持位数 = 8bits，即最大标识的数字为255，若用于分表 最大支持256张表
 *
 * 若使用shardingBits 推荐的配置为
 * snowflake.worker-id-bits=8
 * snowflake.sequence-bits=7
 * snowflake.sharding-bits=8
 *
 * @author lvweijie
 * @date 2022/11/16 1:35 PM
 */
@Data
@Validated
@ConfigurationProperties(prefix = SnowflakeProperties.PREFIX)
public class SnowflakeProperties {

  public static final String PREFIX = "halo.snowflake";

  private Boolean enabled;

  /**
   * 节点名称，推荐和apollo的app.id配置一样
   */
  private String nodeName = System.getProperty("app.id");

  /**
   * zookeeper服务器url
   */
  private String zookeeperUrl;

  /**
   * 起始时间
   */
  private String epochDate = "2024-06-01 00:00:00";

  /**
   * 序号位数
   */
  private Integer sequenceBits = 12;

  /**
   * 机器编号位数
   */
  private Integer workerIdBits = 10;

  /**
   * 分片键位数, 默认0，不启用分片键位
   */
  private Integer shardingBits = 0;

  /**
   * 时钟回拨最大极限等待时间
   */
  private Integer maxTolerateMills = 1000;
}
