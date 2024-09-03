package com.lvwj.halo.dubbo.util;


import com.lvwj.halo.common.constants.SystemConstant;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.JsonUtil;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

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
        Long userId = Func.toLong(RpcContext.getServerAttachment().getAttachment(SystemConstant.USER_ID));
        if (Func.isEmpty(userId)) {
            userId = Optional.ofNullable(getUserInfo()).map(UserInfo::authId).orElse(null);
        }
        return userId;
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
     * 获取TtsID
     */
    public static String getTtsId() {
        RpcContext context = RpcContext.getServerAttachment();
        return context.getAttachment(SystemConstant.TTS_ID);
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
        if (null == userId) return;
        RpcContext context = RpcContext.getServerAttachment();
        context.setAttachment(SystemConstant.USER_ID, userId);
    }

    /**
     * 设置成员ID
     */
    public static void setMemberId(Long memberId) {
        if (null == memberId) return;
        RpcContext context = RpcContext.getServerAttachment();
        context.setAttachment(SystemConstant.MEMBER_ID, memberId);
    }

    /**
     * 设置设备编号
     */
    public static void setDeviceNo(String deviceNo) {
        if (Func.isBlank(deviceNo)) return;
        RpcContext context = RpcContext.getServerAttachment();
        context.setAttachment(SystemConstant.DEVICE_NO, deviceNo);
    }

    /**
     * 设置编码
     */
    public static void setEnocding(String enocding) {
        if (Func.isBlank(enocding)) return;
        RpcContext context = RpcContext.getServerAttachment();
        context.setAttachment(SystemConstant.ENCODING, enocding);
    }

    /**
     * 设置tts id
     */
    public static void setTtsId(String ttsId) {
        if (Func.isBlank(ttsId)) return;
        RpcContext context = RpcContext.getServerAttachment();
        context.setAttachment(SystemConstant.TTS_ID, ttsId);
    }

    public static String getTraceId() {
        String traceId = RpcContext.getServerAttachment().getAttachment(SystemConstant.TRACE_ID);
        if (Func.isBlank(traceId)) {
            traceId = MDC.get(SystemConstant.TRACE_ID);
        }
        if (Func.isBlank(traceId)) {
            traceId = TraceContext.traceId();
        }
        return traceId;
    }

    public static void setTraceId(String traceId) {
        if (Func.isBlank(traceId) || SystemConstant.DEFAULT_TID.equals(traceId)) return;
        RpcContext context = RpcContext.getServerAttachment();
        context.setAttachment(SystemConstant.TRACE_ID, traceId);
    }


    public static String getUserName() {
        return Optional.ofNullable(getUserInfo()).map(UserInfo::name).orElse(null);
    }

    private static UserInfo getUserInfo() {
        Object employeeInfo = RpcContext.getServerAttachment().getObjectAttachment(SystemConstant.EMPLOYEE_INFO);
        if (Func.isNotEmpty(employeeInfo)) {
            return JsonUtil.parse(JsonUtil.toJson(employeeInfo), UserInfo.class);
        }
        return null;
    }

    record UserInfo(String name, String alias, String email, Long authId, Long syncStaffId) implements Serializable {
    }
}
