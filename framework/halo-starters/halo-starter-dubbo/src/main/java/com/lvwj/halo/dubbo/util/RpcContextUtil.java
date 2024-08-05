package com.lvwj.halo.dubbo.util;


import com.lvwj.halo.common.constants.SystemConstant;
import com.lvwj.halo.common.utils.Func;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import static org.apache.dubbo.common.constants.CommonConstants.REMOTE_APPLICATION_KEY;

/**
 *
 *
 * @author lvweijie
 * @date 2024年07月06日 15:17
 */
public class RpcContextUtil {

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
            lang = RpcContext.getServerAttachment().getAttachment(SystemConstant.LOCALE);
        }
        if (!StringUtils.hasLength(lang)) {
            return Locale.CHINA;
        }
        String[] langCountry = lang.split("-");
        return new Locale(langCountry[0], langCountry[1]);
    }

    public static ZoneId getZoneId() {
        String zoneId = RpcContext.getServerAttachment().getAttachment(SystemConstant.X_ZONE_ID);
        if (Func.isBlank(zoneId)) {
            zoneId = RpcContext.getServerAttachment().getAttachment(SystemConstant.ZONE_ID);
        }
        return StringUtils.hasLength(zoneId) ? ZoneId.of(zoneId) : ZoneId.systemDefault();
    }

    /**
     * 获取设备编号
     */
    public static String getDeviceNo() {
        return Func.toStr(RpcContext.getServerAttachment().getAttachment(SystemConstant.DEVICE_NO));
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return Func.toLong(RpcContext.getServerAttachment().getAttachment(SystemConstant.USER_ID));
    }

    /**
     * 获取角色ID(家庭成员ID)
     */
    public static Long geMemberId() {
        return Func.toLong(RpcContext.getServerAttachment().getAttachment(SystemConstant.MEMBER_ID));
    }

    /**
     * 获取会话ID
     */
    public static String getConversationId() {
        RpcContext context = RpcContext.getServerAttachment();
        return context.getAttachment(SystemConstant.CONVERSATION_ID);
    }

    /**
     * 获取编码Enocding
     */
    public static String getEnocding() {
        RpcContext context = RpcContext.getServerAttachment();
        return context.getAttachment(SystemConstant.ENCODING);
    }

    /**
     * 获取采样率SampleRate
     */
    public static String getSampleRate() {
        RpcContext context = RpcContext.getServerAttachment();
        return context.getAttachment(SystemConstant.SAMPLE_RATE);
    }

    /**
     * 设置用户ID
     */
    public static void setUserId(Long userId) {
        RpcContext context = RpcContext.getServerAttachment();
        context.setAttachment(SystemConstant.USER_ID, userId);
    }

    /**
     * 设置成员ID
     */
    public static void setMemberId(Long memberId) {
        RpcContext context = RpcContext.getServerAttachment();
        context.setAttachment(SystemConstant.MEMBER_ID, memberId);
    }

    /**
     * 设置设备编号
     */
    public static void setDeviceNo(String deviceNo) {
        RpcContext context = RpcContext.getServerAttachment();
        context.setAttachment(SystemConstant.DEVICE_NO, deviceNo);
    }

    /**
     * 设置编码
     */
    public static void setEnocding(String enocding) {
        RpcContext context = RpcContext.getServerAttachment();
        context.setAttachment(SystemConstant.ENCODING, enocding);
    }
}
