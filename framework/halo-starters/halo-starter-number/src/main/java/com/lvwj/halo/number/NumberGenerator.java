package com.lvwj.halo.number;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lvwj.halo.number.manager.NumberManager;
import com.lvwj.halo.number.model.RuleParseData;
import com.lvwj.halo.number.util.RuleParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import java.util.List;

/**
 * 分布式业务编号生成器
 *
 * @author lvwj
 * @date 2022-08-16 16:34
 */
@Slf4j
public class NumberGenerator {

  private static NumberManager numberManager;

  public static NumberManager getNumberManager() {
    if (null == numberManager) {
      numberManager = SpringUtil.getBean(NumberManager.class);
    }
    return numberManager;
  }

  public static String gen(String ruleId, String rule) {
    return gen(ruleId, rule, null);
  }

  public static String gen(String ruleId, String rule, List<Object> params) {
    //获取解析规则
    List<RuleParseData> parses = RuleParseUtil.parse(ruleId, rule);
    //处理解析规则
    return process(ruleId, parses, params);
  }

  private static String process(String ruleId, List<RuleParseData> parses, List<Object> params) {
    StringBuilder sb = new StringBuilder();
    if (!CollectionUtils.isEmpty(parses)) {
      for (RuleParseData parseData : parses) {
        switch (parseData.getType()) {
          case TEXT:
            sb.append(parseData.getValue());
            break;
          case DATE:
            sb.append(RuleParseUtil.dateFmt(parseData.getKey(), parseData.getValue()));
            break;
          case PARAMS:
            int idx = Integer.parseInt(parseData.getValue());
            if (idx >= 0 && null != params && idx < params.size()) {
              sb.append(params.get(idx));
            }
            break;
          case FUNC:
            if ("N".equals(parseData.getKey())) {  //顺序数字
              int length = Integer.parseInt(parseData.getValue());
              if (length > 0) {
                // 获取下一个序列号
                Long val = getNumberManager().nextNum(ruleId);
                if (null != val) {
                  String number = String.format("%0" + length + "d", val);
                  sb.append(number);
                }
              }
            } else if ("R".equals(parseData.getKey())) { //随机数字
              int length = Integer.parseInt(parseData.getValue());
              if (length > 0) {
                sb.append(RandomUtil.randomNumbers(length));
              }
            } else {
              // 日期时间特殊规则处理
              sb.append(RuleParseUtil.dateFmt(parseData.getKey(), parseData.getValue()));
            }
            break;
        }
      }
    }
    return sb.toString();
  }
}
