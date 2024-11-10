package com.lvwj.codegen.templateengine.config;

import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import lombok.Getter;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author lvweijie
 * @date 2024年11月06日 14:02
 */
@Getter
public class ConfigBuilder {

    /**
     * 数据库表信息
     */
    private final List<TableInfo> tableInfoList = new ArrayList<>();

    /**
     * 路径配置信息
     */
    private final Map<OutputFile, String> pathInfo = new HashMap<>();

    /**
     * 策略配置信息
     */
    private StrategyConfig strategyConfig;

    /**
     * 全局配置信息
     */
    private GlobalConfig globalConfig;

    /**
     * 注入配置信息
     */
    private InjectionConfig injectionConfig;

    /**
     * 过滤正则
     */
    private static final Pattern REGX = Pattern.compile("[~!/@#$%^&*()+\\\\\\[\\]|{};:'\",<.>?]+");

    /**
     * 包配置信息
     */
    private final PackageConfig packageConfig;

    /**
     * 数据库配置信息
     */
    private final DataSourceConfig dataSourceConfig;

    /**
     * 在构造器中处理配置
     *
     * @param packageConfig    包配置
     * @param dataSourceConfig 数据源配置
     * @param strategyConfig   表配置
     * @param globalConfig     全局配置
     */
    public ConfigBuilder(PackageConfig packageConfig, DataSourceConfig dataSourceConfig, StrategyConfig strategyConfig, GlobalConfig globalConfig, InjectionConfig injectionConfig) {
        this.dataSourceConfig = dataSourceConfig;
        this.strategyConfig = Optional.ofNullable(strategyConfig).orElseGet(GeneratorBuilder::strategyConfig);
        this.globalConfig = Optional.ofNullable(globalConfig).orElseGet(() -> new GlobalConfig.Builder().build());
        this.packageConfig = Optional.ofNullable(packageConfig).orElseGet(GeneratorBuilder::packageConfig);
        this.injectionConfig = Optional.ofNullable(injectionConfig).orElseGet(GeneratorBuilder::injectionConfig);

        com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder cb
                = new com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder(this.packageConfig,
                this.dataSourceConfig,
                this.strategyConfig, null,
                GeneratorBuilder.globalConfigBuilder().outputDir(this.globalConfig.getOutputDir()).dateType(this.globalConfig.getDateType()).build(),
                this.injectionConfig);
        this.pathInfo.putAll(cb.getPathInfo());
        this.tableInfoList.addAll(cb.getTableInfoList());
    }

    /**
     * 判断表名是否为正则表名(这表名规范比较随意,只能尽量匹配上特殊符号)
     *
     * @param tableName 表名
     * @return 是否正则
     * @since 3.5.0
     */
    public static boolean matcherRegTable(String tableName) {
        return REGX.matcher(tableName).find();
    }


    public ConfigBuilder setStrategyConfig(StrategyConfig strategyConfig) {
        this.strategyConfig = strategyConfig;
        return this;
    }


    public ConfigBuilder setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }


    public ConfigBuilder setInjectionConfig(InjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }
}
