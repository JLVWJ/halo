package com.lvwj.halo.common.constants;

import com.lvwj.halo.common.utils.ConcurrentDateFormat;

import java.time.format.DateTimeFormatter;

/**
 * 时间
 */
public class DateTimeConstant {

    private DateTimeConstant(){}

    //=================================== 标准时间格式 =========================================//

    public static final String PATTERN_DATETIME_S = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATETIME_M = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_DATE_M = "yyyy-MM";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_TIME_M = "HH:mm";

    public static final DateTimeFormatter FORMAT_DATETIME_S = DateTimeFormatter.ofPattern(PATTERN_DATETIME_S);
    public static final DateTimeFormatter FORMAT_DATETIME_M = DateTimeFormatter.ofPattern(PATTERN_DATETIME_M);
    public static final DateTimeFormatter FORMAT_DATETIME = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern(PATTERN_DATE);
    public static final DateTimeFormatter FORMAT_DATE_M = DateTimeFormatter.ofPattern(PATTERN_DATE_M);
    public static final DateTimeFormatter FORMAT_TIME = DateTimeFormatter.ofPattern(PATTERN_TIME);
    public static final DateTimeFormatter FORMAT_TIME_M = DateTimeFormatter.ofPattern(PATTERN_TIME_M);


    //=================================== 拼接时间格式 =========================================//

    public static String PATTERN_Y_M_D_H_M_S_S = "yyyyMMddHHmmssSSS";
    public static String PATTERN_Y_M_D_H_M_S = "yyyyMMddHHmmss";
    public static String PATTERN_Y_M_D_H_M = "yyyyMMddHHmm";
    public static String PATTERN_Y_M_D = "yyyyMMdd";
    public static String PATTERN_Y2_M_D = "yyMMdd";
    public static String PATTERN_Y_M = "yyyyMM";
    public static String PATTERN_M_D = "MMdd";
    public static String PATTERN_Y4 = "yyyy";
    public static String PATTERN_Y2 = "yy";
    public static String PATTERN_H_M_S = "HHmmss";
    public static String PATTERN_H_M = "HHmm";
    public static String PATTERN_H = "HH";
    public static String PATTERN_M_S = "mmss";

    public static final DateTimeFormatter FORMAT_Y_M_D_H_M_S_S = DateTimeFormatter.ofPattern(PATTERN_Y_M_D_H_M_S_S);
    public static final DateTimeFormatter FORMAT_Y_M_D_H_M_S = DateTimeFormatter.ofPattern(PATTERN_Y_M_D_H_M_S);
    public static final DateTimeFormatter FORMAT_Y_M_D_H_M = DateTimeFormatter.ofPattern(PATTERN_Y_M_D_H_M);
    public static final DateTimeFormatter FORMAT_Y_M_D = DateTimeFormatter.ofPattern(PATTERN_Y_M_D);
    public static final DateTimeFormatter FORMAT_Y_M = DateTimeFormatter.ofPattern(PATTERN_Y_M);
    public static final DateTimeFormatter FORMAT_H_M_S = DateTimeFormatter.ofPattern(PATTERN_H_M_S);
    public static final DateTimeFormatter FORMAT_Y4 = DateTimeFormatter.ofPattern(PATTERN_Y4);
    public static final DateTimeFormatter FORMAT_Y2_M_D = DateTimeFormatter.ofPattern(PATTERN_Y2_M_D);
    public static final DateTimeFormatter FORMAT_M_D = DateTimeFormatter.ofPattern(PATTERN_M_D);
    public static final DateTimeFormatter FORMAT_H_M = DateTimeFormatter.ofPattern(PATTERN_H_M);
    public static final DateTimeFormatter FORMAT_M_S = DateTimeFormatter.ofPattern(PATTERN_M_S);



    /**
     * 老 date 格式化
     */
    public static final ConcurrentDateFormat DATETIME_FORMAT = ConcurrentDateFormat.of(PATTERN_DATETIME);
    public static final ConcurrentDateFormat DATETIME_MINI_FORMAT = ConcurrentDateFormat.of(PATTERN_Y_M_D_H_M_S);
    public static final ConcurrentDateFormat DATETIME_S_FORMAT = ConcurrentDateFormat.of(PATTERN_DATETIME_S);
    public static final ConcurrentDateFormat DATETIME_S_MINI_FORMAT = ConcurrentDateFormat.of(PATTERN_Y_M_D_H_M_S_S);
    public static final ConcurrentDateFormat DATETIME_M_FORMAT = ConcurrentDateFormat.of(PATTERN_DATETIME_M);
    public static final ConcurrentDateFormat DATETIME_M_MINI_FORMAT = ConcurrentDateFormat.of(PATTERN_Y_M_D_H_M);
    public static final ConcurrentDateFormat DATE_FORMAT = ConcurrentDateFormat.of(PATTERN_DATE);
    public static final ConcurrentDateFormat DATE_MINI_FORMAT = ConcurrentDateFormat.of(PATTERN_Y_M_D);
    public static final ConcurrentDateFormat DATE_M_FORMAT = ConcurrentDateFormat.of(PATTERN_DATE_M);
    public static final ConcurrentDateFormat DATE_M_MINI_FORMAT = ConcurrentDateFormat.of(PATTERN_Y_M);
    public static final ConcurrentDateFormat TIME_FORMAT = ConcurrentDateFormat.of(PATTERN_TIME);
    public static final ConcurrentDateFormat TIME_MINI_FORMAT = ConcurrentDateFormat.of(PATTERN_H_M_S);
    public static final ConcurrentDateFormat TIME_M_FORMAT = ConcurrentDateFormat.of(PATTERN_TIME_M);
    public static final ConcurrentDateFormat TIME_M_MINI_FORMAT = ConcurrentDateFormat.of(PATTERN_H_M);
}
