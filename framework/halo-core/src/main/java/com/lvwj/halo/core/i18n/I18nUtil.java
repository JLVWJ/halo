package com.lvwj.halo.core.i18n;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * 国际化工具类
 *
 * spring.messages.basename = bizI18nMessage,exceptionI18nMessage
 *
 * @author lvweijie
 * @date 2024年01月29日 14:30
 */
public class I18nUtil {
    private I18nUtil() {
    }

    private static volatile MessageSource ms = null;

    private static MessageSource getMessageSource() {
        if (null == ms) {
            synchronized (I18nUtil.class) {
                if (null == ms) {
                    ms = SpringUtil.getBean(MessageSource.class);
                }
            }
        }
        return ms;
    }

    public static String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        try {
            MessageSource messageSource = getMessageSource();
            if (null == messageSource) {
                if (!StringUtils.hasLength(defaultMessage)) {
                    return null;
                }
                return MessageFormat.format(defaultMessage, args);
            } else {
                return messageSource.getMessage(getCode(code), args, defaultMessage, locale);
            }
        } catch (Exception e) {
            return defaultMessage;
        }
    }

    public static String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage) {
        return getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String code, @Nullable Object[] args) {
        return getMessage(code, args, null);
    }

    public static String getMessage(String code, @Nullable String defaultMessage) {
        return getMessage(code, null, defaultMessage);
    }

    private static String getCode(String code) {
        String prefix = SpringUtil.getProperty("spring.messages.code-prefix");
        if (null == prefix || prefix.isEmpty()) {
            return code;
        }
        return prefix + code;
    }
}
