package com.lvwj.codegen.templateengine;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.lvwj.codegen.templateengine.config.ConfigBuilder;
import com.lvwj.codegen.templateengine.config.DomainModelType;
import com.lvwj.codegen.templateengine.config.GlobalConfig;
import com.lvwj.codegen.templateengine.engine.AbstractTemplateEnginePlus;
import com.lvwj.codegen.templateengine.engine.VelocityTemplateEnginePlus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvweijie
 * @date 2024年11月04日 15:44
 */
@Getter
@Slf4j
public class AutoCodeGenerator {

    protected ConfigBuilder config;
    /**
     * 注入配置
     */
    protected InjectionConfig injection;
    /**
     * 数据源配置
     */
    private DataSourceConfig dataSource;
    /**
     * 数据库表配置
     */
    private StrategyConfig strategy;
    /**
     * 包 相关配置
     */
    private PackageConfig packageInfo;
    /**
     * 全局 相关配置
     */
    private GlobalConfig global;

    private AutoCodeGenerator() {
        // 不推荐使用
    }

    /**
     * 构造方法
     *
     * @param dataSourceConfig 数据库配置
     * @since 3.5.0
     */
    public AutoCodeGenerator(DataSourceConfig dataSourceConfig) {
        //这个是必须参数,其他都是可选的,后续去除默认构造更改成final
        this.dataSource = dataSourceConfig;
    }

    /**
     * 注入配置
     *
     * @param injectionConfig 注入配置
     * @return this
     * @since 3.5.0
     */
    public AutoCodeGenerator injection(InjectionConfig injectionConfig) {
        this.injection = injectionConfig;
        return this;
    }

    /**
     * 生成策略
     *
     * @param strategyConfig 策略配置
     * @return this
     * @since 3.5.0
     */
    public AutoCodeGenerator strategy(StrategyConfig strategyConfig) {
        this.strategy = strategyConfig;
        return this;
    }

    /**
     * 指定包配置信息
     *
     * @param packageConfig 包配置
     * @return this
     * @since 3.5.0
     */
    public AutoCodeGenerator packageInfo(PackageConfig packageConfig) {
        this.packageInfo = packageConfig;
        return this;
    }

    /**
     * 指定全局配置
     *
     * @param globalConfig 全局配置
     * @return this
     * @see 3.5.0
     */
    public AutoCodeGenerator global(GlobalConfig globalConfig) {
        this.global = globalConfig;
        return this;
    }

    /**
     * 设置配置汇总
     *
     * @param configBuilder 配置汇总
     * @return this
     * @since 3.5.0
     */
    public AutoCodeGenerator config(ConfigBuilder configBuilder) {
        this.config = configBuilder;
        return this;
    }

    /**
     * 生成代码
     */
    public void execute() {
        this.execute(null);
    }

    /**
     * 生成代码
     *
     * @param templateEngine 模板引擎
     */
    public void execute(AbstractTemplateEnginePlus templateEngine) {
        log.debug("==========================准备生成文件...==========================");
        // 初始化配置
        if (null == config) {
            config = new ConfigBuilder(packageInfo, dataSource, strategy, global, injection);
        }
        if (null == templateEngine) {
            // 为了兼容之前逻辑，采用 Velocity 引擎 【 默认 】
            templateEngine = new VelocityTemplateEnginePlus();
        }
        templateEngine.setConfigPlusBuilder(config);
        // 模板引擎初始化执行文件输出
        templateEngine.init(config).batchOutput().open();
        log.debug("==========================文件生成完成！！！==========================");
    }

    /**
     * 开放表信息、预留子类重写
     *
     * @param config 配置信息
     * @return ignore
     */
    public List<TableInfo> getAllTableInfoList(ConfigBuilder config) {
        return config.getTableInfoList();
    }

    public static void main(String[] args) {
        //数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder("jdbc:mysql://localhost:3306/my_db", "root", "lwj123456").build();

        Map<String, Object> customMap = new HashMap<>();
        Map<String, DomainModelType> domainModelTypeMap = new HashMap<>() {
            {
                put("knowledges", DomainModelType.aggregate);
            }

            {
                put("knowledge_stories", DomainModelType.aggregate);
            }

            {
                put("knowledge_story_segments", DomainModelType.entity);
            }
        };
        customMap.put(DomainModelType.NAME, domainModelTypeMap);
        //注入配置
        InjectionConfig injectionConfig = new InjectionConfig.Builder().customMap(customMap).build();

        //策略配置
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                .addInclude("knowledges", "knowledge_stories", "knowledge_story_segments")
                .serviceBuilder().convertServiceImplFileName(s -> s + "Dao").superServiceImplClass("com.lvwj.halo.mybatisplus.service.impl.TrackServiceImpl")
                .entityBuilder().convertFileName(s -> s + "PO").addIgnoreColumns("id", "create_time", "create_by", "update_time", "update_by", "delete_time", "delete_by", "is_delete")
                .enableLombok().superClass("com.lvwj.halo.mybatisplus.entity.DeleteEntity")
                .mapperBuilder().enableBaseResultMap().enableBaseColumnList().superClass("com.lvwj.halo.mybatisplus.mapper.CustomMapper")
                .controllerBuilder().enableRestStyle()
                .build();

        //全局配置
        GlobalConfig globalConfig = new GlobalConfig.Builder().enableSpringdoc().build();

        //包路径配置
        PackageConfig packageConfig = new PackageConfig.Builder()
                .parent("com.lvwj.codegen")
                .moduleName("module")//模块名称
                .entity("infrastructure.persistence.entity")
                .mapper("infrastructure.persistence.mapper")
                .xml("infrastructure.persistence.mapper.xml")
                .serviceImpl("infrastructure.persistence.service")
                .controller("interfaces.controller")
                .build();

        //代码生成器
        AutoCodeGenerator autoCodeGenerator = new AutoCodeGenerator(dataSourceConfig);
        autoCodeGenerator.global(globalConfig);
        autoCodeGenerator.packageInfo(packageConfig);
        autoCodeGenerator.injection(injectionConfig);
        autoCodeGenerator.strategy(strategyConfig);
        autoCodeGenerator.execute();
        System.out.println("done");
    }
}
