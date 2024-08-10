package com.lvwj.halo.cache.core.constant;

import com.lvwj.halo.common.enums.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lvweijie
 * @date 2024年08月10日 09:34
 */
@Getter
@AllArgsConstructor
public enum HaloCacheType implements IEnum<String> {

    MULTILEVEL("multilevel","多级缓存"),
    LOCAL("local","本地缓存"),
    REMOTE("remote","远程缓存"),
    ;

    private final String code;
    private final String description;
}
