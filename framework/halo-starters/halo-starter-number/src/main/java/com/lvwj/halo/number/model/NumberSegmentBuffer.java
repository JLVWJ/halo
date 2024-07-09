package com.lvwj.halo.number.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存-号码区间段
 *
 * @author lvwj
 * @date 2022-08-11 16:44
 */
public class NumberSegmentBuffer {

  private static final Map<String, Object> LOCKS = new ConcurrentHashMap<>();

  private final Map<String, NumberSegment> BUFFER = new ConcurrentHashMap<>();

  public void put(String ruleId, Long current, Long max) {
    NumberSegment segment = BUFFER.get(ruleId);
    if (null == segment) {
      synchronized (LOCKS.computeIfAbsent(ruleId, s -> new Object())) {
        try {
          segment = BUFFER.get(ruleId);
          if (null == segment) {
            segment = new NumberSegment(ruleId);
            segment.initId(current, max);
            BUFFER.put(ruleId, segment);
          } else {
            segment.loadId(current, max);
          }
        } finally {
          LOCKS.remove(ruleId);
        }
      }
    } else {
      segment.loadId(current, max);
    }
  }

  public Long get(String ruleId) {
    NumberSegment segment = BUFFER.get(ruleId);
    if (null == segment) {
      return null;
    }
    return segment.nextId();
  }

  public Boolean needLoad(String ruleId) {
    NumberSegment segment = BUFFER.get(ruleId);
    if (null == segment) {
      return Boolean.FALSE;
    }
    return segment.needLoad();
  }
}
