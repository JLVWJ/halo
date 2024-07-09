package com.lvwj.halo.mybatisplus.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvwj.halo.common.dto.request.PageQry;
import com.lvwj.halo.common.dto.request.PageSortQry;
import com.lvwj.halo.common.dto.request.SortType;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * mybatis-plus分页查询工具类
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-21 11:06
 */
public class PageUtil {

  public static <T> IPage<T> getPage(PageQry req, boolean searchCount) {
    if (null == req) {
      return null;
    }
    Page<T> page = new Page<>(req.getPageNo(), req.getPageSize(), searchCount);
    if (req instanceof PageSortQry) {
      PageSortQry sortQry = (PageSortQry) req;
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
}
