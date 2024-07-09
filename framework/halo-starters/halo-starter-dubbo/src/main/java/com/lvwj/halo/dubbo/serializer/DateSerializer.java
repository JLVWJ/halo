package com.lvwj.halo.dubbo.serializer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date序列化为字符串，格式：yyyy-MM-dd HH:mm:ss
 * @author lvweijie
 * @date 2024年01月24日 12:21
 */
public class DateSerializer implements ISerializer<Date> {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Object serialize(Object date) {
        if (date instanceof Date) {
            return simpleDateFormat.format((Date) date);
        }
        return date;
    }
}
