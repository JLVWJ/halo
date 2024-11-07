package com.lvwj.halo.mybatisplus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.lvwj.halo.common.utils.DateTimeUtil;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 基础字段数据自动填充器
 *
 * @author lvweijie
 * @date 2023年11月03日 16:19
 */
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_TIME = "updateTime";

    private static final String CREATE_BY = "createBy";
    private static final String UPDATE_BY = "updateBy";
    private static final String IS_DELETE = "isDelete";

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = DateTimeUtil.toDateTime(new Date(), ZoneId.of("UTC"));
        fillStrategy(metaObject, CREATE_TIME, now);
        fillStrategy(metaObject, UPDATE_TIME, now);
        fillStrategy(metaObject, IS_DELETE, 0);
        setVersion(metaObject);

        String currentUser = ThreadLocalUtil.getCurrentUserName();
        if (Func.isNotBlank(currentUser)) {
            fillStrategy(metaObject, CREATE_BY, currentUser);
            fillStrategy(metaObject, UPDATE_BY, currentUser);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = DateTimeUtil.toDateTime(new Date(), ZoneId.of("UTC"));
        String currentUser = ThreadLocalUtil.getCurrentUserName();
        this.setFieldValByName(UPDATE_TIME, now, metaObject);
        if (Func.isNotBlank(currentUser)) {
            fillStrategy(metaObject, UPDATE_BY, currentUser);
        }
    }

    private void setVersion(MetaObject metaObject) {
        TableInfo tableInfo = findTableInfo(metaObject);
        TableFieldInfo tableFieldInfo = tableInfo.getFieldList().stream().filter(TableFieldInfo::isVersion).findFirst().orElse(null);
        if (null == tableFieldInfo) {
            return;
        }
        Class<?> aClass = tableFieldInfo.getPropertyType();
        if (aClass.equals(Long.class)) {
            fillStrategy(metaObject, tableFieldInfo.getColumn(), 0L);
        } else if (aClass.equals(Integer.class)) {
            fillStrategy(metaObject, tableFieldInfo.getColumn(), 0);
        } else if (aClass.equals(Date.class)) {
            fillStrategy(metaObject, tableFieldInfo.getColumn(), new Date());
        }
    }
}

