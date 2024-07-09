package com.lvwj.halo.number.manager;

import com.lvwj.halo.number.constant.NumberConstant;
import com.lvwj.halo.number.model.NumberSegmentBuffer;
import com.lvwj.halo.number.util.RuleParseUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * 号码区间段管理
 *
 * @author lvwj
 * @date 2022-08-28 15:34
 */
@Slf4j
public class NumberSegmentManager extends AbstractNumberManager {

  private final NumberSegmentBuffer BUFFER = new NumberSegmentBuffer();

  @Override
  public Boolean enableAsyncLoad(String ruleId) {
    //步长大于1，才开启异步加载
    return RuleParseUtil.getStep(ruleId) > NumberConstant.STEP_MIN;
  }

  @Override
  protected Long getFromBuffer(String ruleId) {
    return BUFFER.get(ruleId);
  }

  @Override
  protected void addToBuffer(String ruleId, Long current, Long max) {
    BUFFER.put(ruleId, current, max);
  }

  @Override
  protected void asyncLoad(String ruleId) {
    if (!BUFFER.needLoad(ruleId)) {
      return;
    }
    CompletableFuture.runAsync(() -> load(ruleId, false), NumberConstant.LOAD_POOL);
  }
}
