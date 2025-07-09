package com.lvwj.halo.dubbo.serializer;

import com.lvwj.halo.common.utils.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

/**
 * Temporal实现类：LocalDateTime LocalDate LocalTime
 *
 * @author lvweijie
 * @date 2024年01月24日 12:21
 */
public class TemporalSerializer implements ISerializer<Temporal> {

    @Override
    public Object serialize(Object date) {
        if (date instanceof LocalDateTime) {
            return DateTimeUtil.formatDateTime((LocalDateTime) date);
        }
        if (date instanceof LocalDate) {
            return DateTimeUtil.formatDate((LocalDate) date);
        }
        if (date instanceof LocalTime) {
            return DateTimeUtil.formatTime((LocalTime) date);
        }
        return date;
    }
}
