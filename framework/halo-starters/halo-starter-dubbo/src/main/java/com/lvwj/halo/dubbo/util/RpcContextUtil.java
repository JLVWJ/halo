package com.lvwj.halo.dubbo.util;


import com.lvwj.halo.common.constants.SystemConstant;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 *
 * @author lvweijie
 * @date 2024年07月06日 15:17
 */
public class RpcContextUtil {
    private static final String LOCALE = "x-locale";

    private RpcContextUtil() {
    }

    public static void setLocaleContextHolder() {
        LocaleContextHolder.setLocale(getLocale());
        LocaleContextHolder.setTimeZone(TimeZone.getTimeZone(getZoneId()));
    }

    public static void clearLocaleContextHolder() {
        LocaleContextHolder.resetLocaleContext();
    }

    public static String getAppId() {
        return RpcContext.getServerAttachment().getAttachment(SystemConstant.APP_ID);
    }

    public static Locale getLocale() {
        String lang = RpcContext.getServerAttachment().getAttachment(SystemConstant.LANG);
        if (!StringUtils.hasLength(lang)) {
            lang = RpcContext.getServerAttachment().getAttachment(LOCALE);
        }
        if (!StringUtils.hasLength(lang)) {
            return Locale.CHINA;
        }
        String[] langCountry = lang.split("-");
        return new Locale(langCountry[0], langCountry[1]);
    }

    public static ZoneId getZoneId() {
        String zoneId = RpcContext.getServerAttachment().getAttachment(SystemConstant.ZONE_ID);
        return StringUtils.hasLength(zoneId) ? ZoneId.of(zoneId) : ZoneId.systemDefault();
    }
}
