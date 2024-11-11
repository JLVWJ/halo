package com.lvwj.codegen.templateengine.config;

import com.baomidou.mybatisplus.generator.config.IConfigBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

/**
 * @author lvweijie
 * @date 2024年11月04日 15:51
 */
public class GlobalConfig {

    @Getter
    private String outputDir = System.getProperty("os.name").toLowerCase().contains("windows") ? "D://" : System.getProperty("user.dir") + "/src/main/data";

    /**
     * 是否打开输出目录
     */
    @Getter
    private boolean open = true;

    /**
     * 作者
     */
    @Getter
    private String author = "lvweijie";

    /**
     * 应用服务名称
     */
    @Getter
    private String appName = "halo-codegen";

    /**
     * 开启 Kotlin 模式（默认 false）
     */
    @Getter
    private boolean kotlin;

    /**
     * 开启 swagger 模式（默认 false 与 springdoc 不可同时使用）
     */
    private boolean swagger;
    /**
     * 开启 springdoc 模式（默认 false 与 swagger 不可同时使用）
     */
    @Getter
    private boolean springdoc;

    /**
     * 默认开启DDD模式
     */
    @Getter
    private boolean ddd = true;

    /**
     * dubbo接口模式
     */
    @Getter
    private boolean dubbo = true;

    /**
     * openFeign接口模式
     */
    @Getter
    private boolean feign = false;

    /**
     * 时间类型对应策略
     */
    @Getter
    private DateType dateType = DateType.TIME_PACK;

    /**
     * 获取注释日期
     *
     * @since 3.5.0
     */
    private Supplier<String> commentDate = () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


    public boolean isSwagger() {
        // springdoc 设置优先于 swagger
        return !springdoc && swagger;
    }

    public String getCommentDate() {
        return commentDate.get();
    }


    @Getter
    private String superAggregateClass = "com.lvwj.halo.core.domain.entity.Aggregate";
    @Getter
    private String superDomainEntityClass = "com.lvwj.halo.core.domain.entity.Entity";
    @Getter
    private String superValueObjClass = "com.lvwj.halo.core.domain.entity.IValueObj";
    @Getter
    private String superDomainEventClass = "com.lvwj.halo.core.domain.event.DomainEvent";
    @Getter
    private String superIntegrationEventClass = "com.lvwj.halo.core.domain.event.IntegrationEvent";
    @Getter
    private String superConverterClass = "com.lvwj.halo.common.models.entity.IEntityConverter";
    @Getter
    private String superRepositoryClass = "com.lvwj.halo.core.domain.repository.IRepository";
    @Getter
    private String superRepositoryImplClass = "com.lvwj.halo.core.domain.repository.TrackRepository";
    @Getter
    private String responseWrapperClass = "com.lvwj.halo.common.dto.response.R";


    /**
     * 全局配置构建
     *
     * @author nieqiurong 2020/10/11.
     * @since 3.5.0
     */
    public static class Builder implements IConfigBuilder<GlobalConfig> {

        private final GlobalConfig globalConfig;

        public Builder() {
            this.globalConfig = new GlobalConfig();
        }

        /**
         * 禁止打开输出目录
         */
        public GlobalConfig.Builder disableOpenDir() {
            this.globalConfig.open = false;
            return this;
        }

        /**
         * 输出目录
         */
        public GlobalConfig.Builder outputDir(String outputDir) {
            this.globalConfig.outputDir = outputDir;
            return this;
        }

        /**
         * 作者
         */
        public GlobalConfig.Builder author(String author) {
            this.globalConfig.author = author;
            return this;
        }

        /**
         * 应用服务名称
         */
        public GlobalConfig.Builder appName(String appName) {
            this.globalConfig.appName = appName;
            return this;
        }

        /**
         * 开启 kotlin 模式
         */
        public GlobalConfig.Builder enableKotlin() {
            this.globalConfig.kotlin = true;
            return this;
        }

        /**
         * 开启 swagger 模式
         */
        public GlobalConfig.Builder enableSwagger() {
            this.globalConfig.swagger = true;
            return this;
        }

        /**
         * 开启 springdoc 模式
         */
        public GlobalConfig.Builder enableSpringdoc() {
            this.globalConfig.springdoc = true;
            return this;
        }

        /**
         * 关闭 ddd 模式
         */
        public GlobalConfig.Builder disableDDD() {
            this.globalConfig.ddd = false;
            return this;
        }

        /**
         * 开启 openFeign接口 模式
         */
        public GlobalConfig.Builder enableFeign() {
            this.globalConfig.feign = true;
            this.globalConfig.dubbo = false;
            return this;
        }

        /**
         * 开启 dubbo接口 模式
         */
        public GlobalConfig.Builder enableDubbo() {
            this.globalConfig.feign = false;
            this.globalConfig.dubbo = true;
            return this;
        }

        /**
         * 时间类型对应策略
         */
        public GlobalConfig.Builder dateType(DateType dateType) {
            this.globalConfig.dateType = dateType;
            return this;
        }

        /**
         * 注释日期获取处理
         * example: () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
         *
         * @param commentDate 获取注释日期
         * @return this
         * @since 3.5.0
         */
        public GlobalConfig.Builder commentDate(Supplier<String> commentDate) {
            this.globalConfig.commentDate = commentDate;
            return this;
        }

        /**
         * 指定注释日期格式化
         *
         * @param pattern 格式
         * @return this
         * @since 3.5.0
         */
        public GlobalConfig.Builder commentDate(String pattern) {
            return commentDate(() -> new SimpleDateFormat(pattern).format(new Date()));
        }


        /**
         * 聚合根基类
         */
        public GlobalConfig.Builder superAggregateClass(String superClass) {
            this.globalConfig.superAggregateClass = superClass;
            return this;
        }

        /**
         * 领域实体基类
         */
        public GlobalConfig.Builder superDomainEntityClass(String superClass) {
            this.globalConfig.superDomainEntityClass = superClass;
            return this;
        }

        public GlobalConfig.Builder superValueObjClass(String superClass) {
            this.globalConfig.superValueObjClass = superClass;
            return this;
        }

        public GlobalConfig.Builder superDomainEventClass(String superClass) {
            this.globalConfig.superDomainEventClass = superClass;
            return this;
        }

        public GlobalConfig.Builder superIntegrationEventClass(String superClass) {
            this.globalConfig.superIntegrationEventClass = superClass;
            return this;
        }

        public GlobalConfig.Builder superConverterClass(String superClass) {
            this.globalConfig.superConverterClass = superClass;
            return this;
        }

        public GlobalConfig.Builder superRepositoryClass(String superClass) {
            this.globalConfig.superRepositoryClass = superClass;
            return this;
        }

        public GlobalConfig.Builder superRepositoryImplClass(String superClass) {
            this.globalConfig.superRepositoryImplClass = superClass;
            return this;
        }

        /**
         * 响应通用包装类
         */
        public GlobalConfig.Builder responseWrapperClass(String responseWrapperClass) {
            this.globalConfig.responseWrapperClass = responseWrapperClass;
            return this;
        }

        @Override
        public GlobalConfig build() {
            return this.globalConfig;
        }
    }
}
