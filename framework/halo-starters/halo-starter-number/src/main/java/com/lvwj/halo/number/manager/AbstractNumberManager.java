package com.lvwj.halo.number.manager;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.lvwj.halo.number.constant.RuleModeEnum;
import com.lvwj.halo.number.util.RuleParseUtil;
import com.lvwj.halo.redis.RedisTemplatePlus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author lvwj
 * @date 2022-08-28 16:50
 */
public abstract class AbstractNumberManager implements NumberManager {

  private static final Map<String, Object> LOCKS = new ConcurrentHashMap<>();

  @Autowired
  private RedisTemplatePlus redisTemplatePlus;

  public Long nextNum(String ruleId) {
    Long next;
    while (true) {
      next = getFromBuffer(ruleId);
      if (null != next) {
        break;
      }
      synchronized (LOCKS.computeIfAbsent(ruleId, s -> new Object())) {
        try {
          next = getFromBuffer(ruleId);
          if (null != next) {
            break;
          }
          next = load(ruleId, true);
          if (null != next) {
            break;
          }
        } finally {
          LOCKS.remove(ruleId);
        }
      }
    }
    if (enableAsyncLoad(ruleId)) {
      asyncLoad(ruleId);
    }
    return next;
  }

  protected Long load(String ruleId, boolean sync) {
    Long firstNum = null;
    try {
      RuleModeEnum mode = RuleParseUtil.getMode(ruleId);
      String cacheKey = cacheKey(ruleId, mode);
      Integer step = RuleParseUtil.getStep(ruleId);
      //redis累加计数，反回累加后的数字
      Long max = redisTemplatePlus.incrBy(cacheKey, step);
      if (null == max) {
        throw new RuntimeException("redis increment return value is null");
      }
      //日期模式并且是首次初始化时
      if (max.intValue() == step) {
        Date now = new Date();
        long minute = 0; //计算剩余分钟，作为redis key的过期时间
        if (mode == RuleModeEnum.D) {
          minute = DateUtil.between(now, DateUtil.endOfDay(now), DateUnit.MINUTE);
        }
        if (mode == RuleModeEnum.M) {
          minute = DateUtil.between(now, DateUtil.endOfMonth(now), DateUnit.MINUTE);
        }
        if (mode == RuleModeEnum.Y) {
          minute = DateUtil.between(now, DateUtil.endOfYear(now), DateUnit.MINUTE);
        }
        if (minute > 0) {
          redisTemplatePlus.expire(cacheKey, minute + 1, TimeUnit.MINUTES);
        }
      }
      long current = max - step;
      if (sync) {
        firstNum = current + 1;
        current = firstNum;
      }
      if (current < max) {
        addToBuffer(ruleId, current, max);
      }
    } catch (Exception e) {
      throw new RuntimeException("load value error: " + ruleId, e);
    }
    return firstNum;
  }

  protected abstract Long getFromBuffer(String ruleId);

  protected abstract void addToBuffer(String ruleId, Long current, Long max);

  protected abstract void asyncLoad(String ruleId);

}
