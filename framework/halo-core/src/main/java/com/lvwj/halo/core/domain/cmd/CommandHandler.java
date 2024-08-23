package com.lvwj.halo.core.domain.cmd;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 命令处理器
 *
 * @author lvweijie
 * @date 2024年04月01日 13:51
 */
@Component
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandler {

}

