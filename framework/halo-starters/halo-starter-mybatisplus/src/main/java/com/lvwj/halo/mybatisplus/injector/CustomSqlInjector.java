package com.lvwj.halo.mybatisplus.injector;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.lvwj.halo.mybatisplus.injector.methods.*;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 自定义mapper method sql注入
 *
 * @author lvwj
 * @version 1.0.0
 * @date 2022-12-07 17:58
 */
public class CustomSqlInjector extends DefaultSqlInjector {

  @Override
  public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
    List<AbstractMethod> methodList = new ArrayList<>();
    methodList.add(new InsertIgnore());
    methodList.add(new Replace());
    methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
    methodList.add(new UpdateBatch());
    methodList.addAll(super.getMethodList(configuration, mapperClass, tableInfo));
    return Collections.unmodifiableList(methodList);
  }
}
