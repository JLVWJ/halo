package com.lvwj.halo.number.util;

import com.lvwj.halo.common.constants.DateTimeConstant;
import com.lvwj.halo.common.utils.DateTimeUtil;
import com.lvwj.halo.number.constant.NumberConstant;
import com.lvwj.halo.number.constant.RuleModeEnum;
import com.lvwj.halo.number.constant.RuleTypeEnum;
import com.lvwj.halo.number.model.RuleParseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lvwj
 * @date 2022-08-11 16:59
 */
@Slf4j
public class RuleParseUtil {

  //本地锁
  private static final Map<String, Object> LOCKS = new ConcurrentHashMap<>();

  //缓存规则解析结果
  private static final Map<String, List<RuleParseData>> RULE_CACHE = new ConcurrentHashMap<>();

  public static List<RuleParseData> parse(String ruleId, String rule) {
    String key = ruleId + ":" + rule;
    List<RuleParseData> parses = RULE_CACHE.get(key);
    if (null == parses) {
      synchronized (LOCKS.computeIfAbsent(key, s -> new Object())) {
        try {
          parses = RULE_CACHE.get(key);
          if (null == parses) {
            // 解析规则
            parses = parse(rule);
            RULE_CACHE.put(key, parses);
          }
        } finally {
          LOCKS.remove(ruleId);
        }
      }
    }
    return parses;
  }

  /**
   * 解析规则
   *
   * @param rule 规则
   * @author lvwj
   * @date 2022-08-12 09:48
   */
  public static List<RuleParseData> parse(String rule) {
    List<RuleParseData> list = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    //遍历rule字符，解析出RuleParseData集合
    for (int i = 0; i < rule.length(); i++) {
      char ch = rule.charAt(i);
      // 空格不处理，清理掉
      if (' ' == ch) {
        continue;
      }
      String s = sb.toString();
      if ('{' == ch) {
        if (StringUtils.isNotBlank(s)) {
          // 文本类型解析
          list.add(new RuleParseData(RuleTypeEnum.TEXT, null, s));
        }
        sb = new StringBuilder();
      } else if ('}' == ch) {
        if (NumberUtils.isCreatable(s)) {
          // 参数类型解析
          list.add(new RuleParseData(RuleTypeEnum.PARAMS, null, s));
        } else if (s.contains(":")) {
          int idx = s.indexOf(":");
          String key = s.substring(0, idx);
          String val = s.substring(idx + 1);
          // 特殊函数类型解析
          list.add(new RuleParseData(RuleTypeEnum.FUNC, key, val));
        } else {
          // 默认日期类型解析
          list.add(new RuleParseData(RuleTypeEnum.DATE, s, null));
        }
        sb = new StringBuilder();
      } else {
        // 追加连续字符
        sb.append(ch);
      }
    }
    //解析剩余文本
    String s = sb.toString();
    if (StringUtils.isNotBlank(s)) {
      // 文本类型解析
      list.add(new RuleParseData(RuleTypeEnum.TEXT, s, s));
    }
    return list;
  }

  public static Integer getStep(String ruleId) {
    Integer step = null;
    String[] split = ruleId.split("#");
    if (split.length > 2) {
      try {
        step = Integer.parseInt(split[2]);
      } catch (Exception e) {
        log.error("编号规则：" + ruleId + "格式有误, " + split[2] + "转数字失败！");
      }
    }
    if (null == step) {
      step = Optional.ofNullable(NumberConstant.STEP).orElse(NumberConstant.DEFAULT_STEP);
    }
    if (step < NumberConstant.STEP_MIN) {
      step = NumberConstant.STEP_MIN;
    }
    if (step > NumberConstant.STEP_MAX) {
      step = NumberConstant.STEP_MAX;
    }
    return step;
  }

  public static Double getLoadFactor(String ruleId) {
    Double loadFactor = null;
    String[] split = ruleId.split("#");
    if (split.length > 3) {
      try {
        loadFactor = Double.parseDouble(split[3]);
      } catch (Exception e) {
        log.error("编号规则：" + ruleId + "格式有误, " + split[3] + "转数字失败！");
      }
    }
    if (null == loadFactor) {
      loadFactor = NumberConstant.LOAD_FACTOR;
    }
    if (loadFactor <= 0 || loadFactor > 1) {
      loadFactor = NumberConstant.DEFAULT_LOAD_FACTOR;
    }
    return loadFactor;
  }

  public static RuleModeEnum getMode(String ruleId) {
    RuleModeEnum mode = RuleModeEnum.G;
    String[] split = ruleId.split("#");
    if (split.length > 1) {
      if (split[1].equalsIgnoreCase(RuleModeEnum.D.name())) {
        mode = RuleModeEnum.D;
      } else if (split[1].equalsIgnoreCase(RuleModeEnum.M.name())) {
        mode = RuleModeEnum.M;
      } else if (split[1].equalsIgnoreCase(RuleModeEnum.Y.name())) {
        mode = RuleModeEnum.Y;
      }
    }
    return mode;
  }


  /**
   * 日期规则格式化
   *
   * @param key
   * @param val
   * @return java.lang.String
   * @author lvwj
   * @date 2022-08-12 09:49
   */
  public static String dateFmt(String key, String val) {
    String str = "";
    LocalDateTime now = LocalDateTime.now();
    if ("DT".equals(key)) {
      if (null != val) {
        // 处理自定义表达式
        str = DateTimeUtil.format(now, val);
      } else {
        str = DateTimeUtil.format(now, DateTimeConstant.PATTERN_Y_M_D_H_M_S);
      }
    } else if ("D".equals(key)) {
      if (null != val) {
        if ("y2".equalsIgnoreCase(val)) {
          str = DateTimeUtil.format(now, DateTimeConstant.PATTERN_Y2_M_D);
        } else if ("y-".equalsIgnoreCase(val)) {
          str = DateTimeUtil.format(now, DateTimeConstant.PATTERN_M_D);
        } else if ("d-".equalsIgnoreCase(val)) {
          str = DateTimeUtil.format(now, DateTimeConstant.PATTERN_Y_M);
        } else if ("y+".equalsIgnoreCase(val)) {
          str = DateTimeUtil.format(now, DateTimeConstant.PATTERN_Y4);
        } else if ("y2+".equalsIgnoreCase(val)) {
          str = DateTimeUtil.format(now, DateTimeConstant.PATTERN_Y2);
        }
      } else {
        str = DateTimeUtil.format(now, DateTimeConstant.PATTERN_Y_M_D);
      }
    } else if ("T".equals(key)) {
      if (null != val) {
        if ("h-".equalsIgnoreCase(val)) {
          str = DateTimeUtil.format(now, DateTimeConstant.PATTERN_M_S);
        } else if ("s-".equalsIgnoreCase(val)) {
          str = DateTimeUtil.format(now, DateTimeConstant.PATTERN_H_M);
        }
      } else {
        str = DateTimeUtil.format(now, DateTimeConstant.PATTERN_H_M_S);
      }
    }
    return str;
  }
}
