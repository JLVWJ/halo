package com.lvwj.halo.core.snowflake;

import com.google.common.base.Preconditions;
import com.lvwj.halo.common.utils.DateTimeUtil;
import lombok.SneakyThrows;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

/**
 * 雪花算法唯一ID生成，支持分片键位
 *
 * @author lvweijie
 * @date 2022/08/21
 **/
public class SnowflakeGenerator {

  private static final long EPOCH;

  private final long sequenceMask;

  private final long timestampLeftShiftBits;

  private final long workerIdLeftShiftBits;

  private final long shardingMaxValue;

  private final int maxTolerateTimeDifferenceMilliseconds;

  private final long workerId;
  private final long shardingBits;
  private long sequence;

  private long lastMilliseconds;
  private final long customEpoch;

  static {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2024, Calendar.MAY, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    EPOCH = calendar.getTimeInMillis();
  }

  public SnowflakeGenerator(SnowflakeProperties properties, Long workerId) {
    String epochDateStr = properties.getEpochDate();
    if (epochDateStr == null) {
      this.customEpoch = EPOCH;
    } else {
      LocalDateTime dateTime = DateTimeUtil.parseDateTime(epochDateStr, DateTimeUtil.PATTERN_DATETIME);
      this.customEpoch = DateTimeUtil.toInstant(dateTime).toEpochMilli();
    }
    long sequenceBits = properties.getSequenceBits();
    long workerIdBits = properties.getWorkerIdBits();
    shardingBits = properties.getShardingBits();

    Preconditions.checkArgument(sequenceBits >= 1, "Illegal sequence.bits");
    Preconditions.checkArgument(workerIdBits >= 1, "Illegal worker.id.bits");
    Preconditions.checkArgument(shardingBits >= 0, "Illegal sharding.bits");
    Preconditions.checkArgument((sequenceBits + workerIdBits + shardingBits) < 50, "Illegal timestamp bits");

    sequenceMask = (1L << sequenceBits) - 1;
    timestampLeftShiftBits = workerIdBits + shardingBits + sequenceBits;
    workerIdLeftShiftBits = shardingBits + sequenceBits;
    shardingMaxValue = shardingBits == 0 ? 0 : (1L << shardingBits) - 1;
    long workerIdMaxValue = (1L << workerIdBits) - 1;

    this.workerId = workerId;
    Preconditions.checkArgument(workerId >= 0L && workerId <= workerIdMaxValue, "Illegal work id");
    maxTolerateTimeDifferenceMilliseconds = properties.getMaxTolerateMills();
  }

  /**
   * 获取雪花id
   */
  public synchronized long nextId() {
    if (shardingBits > 0) {
      throw new UnsupportedOperationException("分表场景需要有shardValue，建议使用 nextId(long shardValue) 方法获取");
    }
    return nextId(0L);
  }

  /**
   * 获取雪花id, 配置：sharding.bits > 0
   */
  public synchronized long nextId(long shardValue) {
    long currentMilliseconds = System.currentTimeMillis();
    //解决时钟回拨问题，休眠之后重新获取当前时间戳。
    if (waitTolerateTimeDifferenceIfNeed(currentMilliseconds)) {
      currentMilliseconds = System.currentTimeMillis();
    }

    //如果上一个timestamp与新产生的相等，则sequence加1; 对新的timestamp，sequence从0开始
    if (lastMilliseconds == currentMilliseconds) {
      sequence = (sequence + 1) & sequenceMask;
      if (0L == sequence) {
        currentMilliseconds = waitUntilNextTime(currentMilliseconds);
      }
    } else {
      sequence = 0;
    }
    lastMilliseconds = currentMilliseconds;
    long mill = currentMilliseconds - customEpoch;
    long shard = shardValue & shardingMaxValue;
    //时间戳 + work id + 序号 + 分片键
    return (mill << timestampLeftShiftBits) | (workerId << workerIdLeftShiftBits) | (sequence << shardingBits) | shard;
  }

  @SneakyThrows
  private boolean waitTolerateTimeDifferenceIfNeed(final long currentMilliseconds) {
    if (lastMilliseconds <= currentMilliseconds) {
      return false;
    }
    long timeDifferenceMilliseconds = lastMilliseconds - currentMilliseconds;
    Preconditions.checkState(timeDifferenceMilliseconds < maxTolerateTimeDifferenceMilliseconds,
            "Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds",
            lastMilliseconds, currentMilliseconds);
    Thread.sleep(timeDifferenceMilliseconds);
    return true;
  }

  private long waitUntilNextTime(final long lastTime) {
    long result = System.currentTimeMillis();
    while (result <= lastTime) {
      result = System.currentTimeMillis();
    }
    return result;
  }

  /**
   * 反解获取时间
   */
  public LocalDateTime getDateTime(long id) {
    long diffTime = id >> timestampLeftShiftBits;
    return Instant.ofEpochMilli(diffTime + customEpoch).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }
}