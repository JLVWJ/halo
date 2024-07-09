package com.lvwj.halo.number.manager;

import com.lvwj.halo.common.utils.DateTimeUtil;
import com.lvwj.halo.number.constant.NumberConstant;
import com.lvwj.halo.number.constant.RuleModeEnum;

import java.time.LocalDateTime;

/**
 * @author lvwj
 * @date 2022-08-28 15:31
 */
public interface NumberManager {

  Long nextNum(String ruleId);

  default Boolean enableAsyncLoad(String ruleId) {
    return Boolean.TRUE;
  }

  default String cacheKey(String ruleId, RuleModeEnum mode) {
    String cacheKey = ruleId;
    LocalDateTime now = LocalDateTime.now();
    if (mode == RuleModeEnum.D) {
      cacheKey = ruleId + ":" + DateTimeUtil.format(now, DateTimeUtil.PATTERN_Y_M_D);
    } else if (mode == RuleModeEnum.M) {
      cacheKey = ruleId + ":" + DateTimeUtil.format(now, DateTimeUtil.PATTERN_Y_M);
    } else if (mode == RuleModeEnum.Y) {
      cacheKey = ruleId + ":" + DateTimeUtil.format(now, DateTimeUtil.PATTERN_Y4);
    }
    cacheKey = NumberConstant.CACHE_KEY_PREFIX + cacheKey;
    return cacheKey;
  }
}
