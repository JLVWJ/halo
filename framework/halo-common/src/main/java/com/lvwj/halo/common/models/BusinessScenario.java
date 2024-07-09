package com.lvwj.halo.common.models;

import com.lvwj.halo.common.constants.ExtensionConstant;
import lombok.Getter;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentLruCache;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Getter
public class BusinessScenario {

  public static final String SPLIT = "#";
  public static final String IDENTIFY_TPL = "%s#%s#%s";

  // 增加场景缓存，提高查找效率，减少重复创建对象进而减少GC
  private static final ConcurrentLruCache<String, BusinessScenario> CACHE = new ConcurrentLruCache<>(1024, scenarioStr -> {
    String[] arr = StringUtils.delimitedListToStringArray(scenarioStr, SPLIT);
    return new BusinessScenario(arr[0], arr[1], arr[2]);
  });

  private final String business;
  private final String useCase;
  private final String scenario;

  private BusinessScenario(String business, String useCase, String scenario) {
    this.business = business;
    this.useCase = useCase;
    this.scenario = scenario;
  }

  public static BusinessScenario of(String business, String useCase, String scenario) {
    Assert.doesNotContain(business, SPLIT, "business must not contain #");
    Assert.doesNotContain(useCase, SPLIT, "useCase must not contain #");
    Assert.doesNotContain(scenario, SPLIT, "scenario must not contain #");

    return CACHE.get(getScenarioId(business, useCase, scenario));
  }

  public static BusinessScenario of(String business, String useCase) {
    return of(business, useCase, ExtensionConstant.DEFAULT_SCENARIO);
  }

  public static BusinessScenario of(String business) {
    return of(business, ExtensionConstant.DEFAULT_USE_CASE, ExtensionConstant.DEFAULT_SCENARIO);
  }

  public static BusinessScenario of() {
    return of(ExtensionConstant.DEFAULT_BUSINESS, ExtensionConstant.DEFAULT_USE_CASE,
            ExtensionConstant.DEFAULT_SCENARIO);
  }

  public static String getScenarioId(String business, String useCase, String scenario) {
    return String.format(IDENTIFY_TPL, business, useCase, scenario);
  }

  @Override
  public String toString() {
    return String.format(IDENTIFY_TPL, business, useCase, scenario);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessScenario that = (BusinessScenario) o;
    return business.equals(that.business) && useCase.equals(that.useCase) && scenario.equals(that.scenario);
  }

  @Override
  public int hashCode() {
    return Objects.hash(business, useCase, scenario);
  }

  public int order() {
    int result = 0;
    result += this.business.equals(ExtensionConstant.DEFAULT_BUSINESS) ? 99 : 1;
    result += this.useCase.equals(ExtensionConstant.DEFAULT_USE_CASE) ? 98 : 1;
    result += this.scenario.equals(ExtensionConstant.DEFAULT_SCENARIO) ? 97 : 1;
    return result;
  }
}
