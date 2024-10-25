package com.lvwj.halo.mybatisplus.utils;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvwj.halo.common.dto.request.PageQry;
import com.lvwj.halo.common.dto.request.PageSortQry;
import com.lvwj.halo.common.dto.request.SortType;
import com.lvwj.halo.common.dto.response.PR;
import com.lvwj.halo.common.utils.CollectionUtil;
import com.lvwj.halo.common.utils.Func;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


/**
 * mybatis-plus分页查询工具类
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-21 11:06
 */
public class PageUtil {

  public static <T> IPage<T> getPage(PageQry req) {
    return getPage(req, true);
  }

  public static <T> IPage<T> getPage(PageQry req, OrderItem defaultOrderItem) {
    return getPage(req, true, defaultOrderItem);
  }

  public static <T> IPage<T> getPage(PageQry req, boolean searchCount) {
    return getPage(req, searchCount, null);
  }

  public static <T> IPage<T> getPage(PageQry req, boolean searchCount, OrderItem defaultOrderItem) {
    if (null == req) {
      return null;
    }
    Page<T> page = new Page<>(req.getPageNum(), req.getPageSize(), searchCount);
    if (req instanceof PageSortQry sortQry) {
      if (CollectionUtils.isEmpty(sortQry.getSorts())) {
        defaultOrderItem = Optional.ofNullable(defaultOrderItem).orElseGet(() -> OrderItem.desc("id"));
        page.addOrder(defaultOrderItem);
      } else {
        List<OrderItem> items = new ArrayList<>(sortQry.getSorts().size());
        for (PageSortQry.Sort sort : sortQry.getSorts()) {
          if (!StringUtils.hasLength(sort.getSortBy())) continue;
          String underlineCase = StrUtil.toUnderlineCase(sort.getSortBy());
          items.add(sort.getSortType().equals(SortType.ASC.getCode()) ? OrderItem.asc(underlineCase) : OrderItem.desc(underlineCase));
        }
        page.addOrder(items);
      }
    }
    return page;
  }

  public static <PO, VO> PR<VO> toPR(IPage<PO> page, Function<PO, VO> func) {
    if (Func.isEmpty(page.getRecords())) {
      return PR.success((int) page.getCurrent(), (int) page.getSize());
    }
    if (null == func) throw new NullPointerException("func is null!");
    List<VO> list = new ArrayList<>(page.getRecords().size());
    page.getRecords().forEach(p -> list.add(func.apply(p)));
    return PR.success((int) page.getTotal(), (int) page.getCurrent(), (int) page.getSize(), list);
  }

  public static <O, T> IPage<T> convert(IPage<O> page, Function<O, T> func) {
    if (page == null) {
      return null;
    }
    if (null == func) throw new NullPointerException("func is null!");
    List<O> records = page.getRecords();
    IPage<T> targetPage = new Page<>();
    targetPage.setCurrent(page.getCurrent());
    targetPage.setPages(page.getPages());
    targetPage.setSize(page.getSize());
    targetPage.setTotal(page.getTotal());
    targetPage.setRecords(CollectionUtil.map(records, func));
    return targetPage;
  }
}
