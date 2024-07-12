package com.lvwj.halo.mybatisplus.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvwj.halo.common.dto.request.PageQry;
import com.lvwj.halo.common.dto.request.PageSortQry;
import com.lvwj.halo.common.dto.request.SortType;
import com.lvwj.halo.common.dto.response.PR;
import com.lvwj.halo.common.utils.Func;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
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

  public static <T> IPage<T> getPage(PageQry req, boolean searchCount) {
    if (null == req) {
      return null;
    }
    Page<T> page = new Page<>(req.getPageNo(), req.getPageSize(), searchCount);
    if (req instanceof PageSortQry sortQry) {
      if (CollectionUtils.isEmpty(sortQry.getSorts())) {
        //默认按id倒序排序
        page.addOrder(OrderItem.desc("id"));
      } else {
        List<OrderItem> items = new ArrayList<>(sortQry.getSorts().size());
        for (PageSortQry.Sort sort : sortQry.getSorts()) {
          items.add(sort.getSortType().equals(SortType.ASC.getCode()) ? OrderItem.asc(sort.getSortBy()) : OrderItem.desc(sort.getSortBy()));
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
}
