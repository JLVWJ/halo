package com.lvwj.halo.mybatisplus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.lvwj.halo.mybatisplus.handler.MyMetaObjectHandler;
import com.lvwj.halo.mybatisplus.injector.CustomSqlInjector;
import com.lvwj.halo.mybatisplus.plugins.MyBlockAttackInnerInterceptor;
import com.lvwj.halo.mybatisplus.config.prop.MybatisPlusExtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * MybatisPlus配置类
 *
 * @author lvweijie
 * @date 2023年12月06日 17:55
 */
@AutoConfiguration
@EnableConfigurationProperties({MybatisPlusExtProperties.class})
public class MybatisPlusExtConfiguration {

    @Autowired
    private MybatisPlusExtProperties mybatisPlusExtProperties;

    @Autowired(required = false)
    private MyBlockAttackInnerInterceptor myBlockAttackInnerInterceptor;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        if (null != myBlockAttackInnerInterceptor) {
            interceptor.addInnerInterceptor(myBlockAttackInnerInterceptor);
        }
        return interceptor;
    }

    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setOverflow(mybatisPlusExtProperties.getOverflow());
        paginationInnerInterceptor.setMaxLimit(mybatisPlusExtProperties.getPageLimit());
        return paginationInnerInterceptor;
    }

    @Bean
    @ConditionalOnProperty(name = "halo.mybatis-plus.block-attack.enable", havingValue = "true")
    public MyBlockAttackInnerInterceptor myBlockAttackInnerInterceptor() {
        return new MyBlockAttackInnerInterceptor();
    }

    @Bean
    public MetaObjectHandler myMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }

    @Bean
    public ISqlInjector customSqlInjector() {
        return new CustomSqlInjector();
    }
}
