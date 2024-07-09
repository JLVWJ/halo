package com.lvwj.halo.number.model;

import com.lvwj.halo.number.constant.RuleModeEnum;
import com.lvwj.halo.number.util.RuleParseUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 号码区间段
 *
 * @author lvwj
 * @date 2022-08-26 13:52
 */
@Data
@Slf4j
public class NumberSegment implements Serializable {

  private final String ruleId;

  //号码区间段数组
  private AtomicReferenceArray<Section> sections;

  //sections的下标
  private volatile int currentIndex = 0;

  private final ReadWriteLock rwl = new ReentrantReadWriteLock();

  //异步加载标识
  private AtomicBoolean asyncLoaded = new AtomicBoolean(false);

  public NumberSegment(String ruleId) {
    Assert.notNull(ruleId, "编号规则不能为空！");
    this.ruleId = ruleId;
    this.sections = new AtomicReferenceArray<>(2);
  }

  /**
   * 当前区间段的号码已用完 或 按天/月/年模式但是号段生成时间不是当天/月/年(说明号段已过期)
   *
   * @author lvwj
   * @date 2022-08-28 18:47
   */
  public Boolean isCurrentOver() {
    Section section = sections.get(currentIndex);
    RuleModeEnum mode = RuleParseUtil.getMode(ruleId);
    //号段用完 或 按天/月/年模式但是号段生成时间不是当天/月/年(说明号段已过期)
    return null == section
            || section.isOver()
            || mode.isDayMode() && !section.isCurrentDay()
            || mode.isMonthMode() && !section.isCurrentMonth()
            || mode.isYearMode() && !section.isCurrentYear()
            ;
  }

  /**
   * 下个区间段的号码已用完 或 按天/月/年模式但是号段生成时间不是当天/月/年(说明号段已过期)
   *
   * @author lvwj
   * @date 2022-08-28 18:47
   */
  public Boolean isNextOver() {
    int nextIndex = currentIndex == 0 ? 1 : 0;
    Section section = sections.get(nextIndex);
    RuleModeEnum mode = RuleParseUtil.getMode(ruleId);
    //号段用完 或 按天/月/年模式但是号段生成时间不是当天/月/年(说明号段已过期)
    return null == section
            || section.isOver()
            || mode.isDayMode() && !section.isCurrentDay()
            || mode.isMonthMode() && !section.isCurrentMonth()
            || mode.isYearMode() && !section.isCurrentYear()
            ;
  }

  /**
   * true 表示俩个区间段的号码都用完了 或 都过期了
   *
   * @author lvwj
   * @date 2022-08-28 18:47
   */
  public Boolean isAllOver() {
    return isCurrentOver() && isNextOver();
  }

  /**
   * 剩余号码数量小于等于阈值(步长*加载因子) 且 没有异步加载过 ，则需要加载
   *
   * @author lvwj
   * @date 2022-08-28 18:47
   */
  public Boolean needLoad() {
    return sections.get(currentIndex).remain() <= RuleParseUtil.getStep(ruleId) * RuleParseUtil.getLoadFactor(ruleId)
            && !asyncLoaded.get();
  }

  public Long incrAndGet() {
    return sections.get(currentIndex).incrAndGet();
  }

  /**
   * 获取下一个ID
   *
   * @author lvwj
   * @date 2022-08-26 17:14
   */
  public Long nextId() {
    if (isAllOver()) {
      return null;
    }
    rwl.readLock().lock();
    try {
      if (isCurrentOver()) {
        rwl.readLock().unlock();
        rwl.writeLock().lock();
        try {
          if (isCurrentOver()) {
            //切换区间段下标 并重置异步加载标识为false
            currentIndex = currentIndex == 0 ? 1 : 0;
            asyncLoaded.set(false);
            if (isCurrentOver()) {
              return null;
            }
          }
        } finally {
          rwl.writeLock().unlock();
        }
        rwl.readLock().lock();
      }
      return incrAndGet();
    } finally {
      rwl.readLock().unlock();
    }
  }

  /**
   * 初始化下标为0的区间段号码
   *
   * @author lvwj
   * @date 2022-08-28 18:45
   */
  public void initId(Long current, Long max) {
    sections.set(0, new Section(max, current));
    //sections.set(1, new Section());
  }

  /**
   * 加载另一个区间段的号码
   *
   * @author lvwj
   * @date 2022-08-28 18:46
   */
  public void loadId(Long current, Long max) {
    if (!needLoad()) {
      return;
    }
    if (asyncLoaded.compareAndSet(false, true)) {
      int nextIndex = currentIndex == 0 ? 1 : 0;
      sections.set(nextIndex, new Section(max, current));
    }
  }


  /**
   * 号码区间段
   *
   * @author lvwj
   * @date 2022-08-28 18:49
   */
  static final class Section {

    private final Long max;
    private final AtomicLong current;
    private final LocalDate createDay;

    public Section() {
      this(0L, 0L);
    }

    public Section(Long max, Long current) {
      this.max = max;
      this.current = new AtomicLong(current);
      this.createDay = LocalDate.now();
    }

    public Long remain() {
      return max - current.get();
    }

    public Boolean isOver() {
      return max <= current.get();
    }

    public Boolean isCurrentDay() {
      return createDay.equals(LocalDate.now());
    }

    public Boolean isCurrentMonth() {
      return createDay.getMonthValue() == (LocalDate.now().getMonthValue());
    }

    public Boolean isCurrentYear() {
      return createDay.getYear() == LocalDate.now().getYear();
    }

    public Long incrAndGet() {
      return current.incrementAndGet();
    }
  }
}
