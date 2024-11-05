package com.lvwj.codegen.templateengine.template;

import com.baomidou.mybatisplus.generator.ITemplate;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.BaseBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.function.ConverterFileName;
import com.baomidou.mybatisplus.generator.util.ClassUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * DDD的仓储接口和实现类
 *
 * @author lvweijie
 * @date 2024年11月04日 15:02
 */
@Getter
public class Repository implements ITemplate {

    private static final String TEMPLATE_REPOSITORY = "/templates/repository.java";
    private static final String TEMPLATE_REPOSITORY_IMPL = "/templates/repositoryImpl.java";
    private static final String SUPER_REPOSITORY_CLASS = "com.lvwj.halo.core.domain.repository.IRepository";
    private static final String SUPER_REPOSITORY_IMPL_CLASS = "com.lvwj.halo.core.domain.repository.TrackRepository";
    
    private String repositoryTemplate = TEMPLATE_REPOSITORY;
    
    private String repositoryImplTemplate = TEMPLATE_REPOSITORY_IMPL;

    /**
     * 自定义继承的Repository类全称，带包名
     */
    private String superRepositoryClass = SUPER_REPOSITORY_CLASS;

    /**
     * 自定义继承的RepositoryImpl类全称，带包名
     */
    private String superRepositoryImplClass = SUPER_REPOSITORY_IMPL_CLASS;

    /**
     * 转换输出Repository文件名称
     */
    private ConverterFileName converterRepositoryFileName = (entityName -> "I" + entityName + "Repository");

    /**
     * 转换输出RepositoryImpl文件名称
     */
    private ConverterFileName converterRepositoryImplFileName = (entityName -> entityName + "RepositoryImpl");

    /**
     * 是否覆盖已有文件（默认 false）
     *
     * @since 3.5.2
     */
    @Getter
    private boolean fileOverride;


    @Override
    public Map<String, Object> renderData(TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>();
        data.put("superRepositoryClassPackage", this.superRepositoryClass);
        data.put("superRepositoryClass", ClassUtils.getSimpleName(this.superRepositoryClass));
        data.put("superRepositoryImplClassPackage", this.superRepositoryImplClass);
        data.put("superRepositoryImplClass", ClassUtils.getSimpleName(this.superRepositoryImplClass));
        return data;
    }


    public static class Builder extends BaseBuilder {

        private final Repository repository = new Repository();

        public Builder(StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        /**
         * Repository接口父类
         *
         * @param clazz 类
         * @return this
         */
        public Repository.Builder superRepositoryClass(Class<?> clazz) {
            return superRepositoryClass(clazz.getName());
        }

        /**
         * Repository接口父类
         *
         * @param superRepositoryClass 类名
         * @return this
         */
        public Repository.Builder superRepositoryClass(String superRepositoryClass) {
            this.repository.superRepositoryClass = superRepositoryClass;
            return this;
        }

        /**
         * Repository实现类父类
         *
         * @param clazz 类
         * @return this
         */
        public Repository.Builder superRepositoryImplClass(Class<?> clazz) {
            return superRepositoryImplClass(clazz.getName());
        }

        /**
         * Repository实现类父类
         *
         * @param superRepositoryImplClass 类名
         * @return this
         */
        public Repository.Builder superRepositoryImplClass(String superRepositoryImplClass) {
            this.repository.superRepositoryImplClass = superRepositoryImplClass;
            return this;
        }

        /**
         * 转换输出Repository接口文件名称
         *
         * @param converter 　转换处理
         * @return this
         * @since 3.5.0
         */
        public Repository.Builder convertRepositoryFileName(ConverterFileName converter) {
            this.repository.converterRepositoryFileName = converter;
            return this;
        }

        /**
         * 转换输出Repository实现类文件名称
         *
         * @param converter 　转换处理
         * @return this
         * @since 3.5.0
         */
        public Repository.Builder convertRepositoryImplFileName(ConverterFileName converter) {
            this.repository.converterRepositoryImplFileName = converter;
            return this;
        }

        /**
         * 格式化Repository接口文件名称
         *
         * @param format 　格式
         * @return this
         * @since 3.5.0
         */
        public Repository.Builder formatRepositoryFileName(String format) {
            return convertRepositoryFileName((entityName) -> String.format(format, entityName));
        }

        /**
         * 格式化Repository实现类文件名称
         *
         * @param format 　格式
         * @return this
         * @since 3.5.0
         */
        public Repository.Builder formatRepositoryImplFileName(String format) {
            return convertRepositoryImplFileName((entityName) -> String.format(format, entityName));
        }

        /**
         * 覆盖已有文件
         */
        public Repository.Builder enableFileOverride() {
            this.repository.fileOverride = true;
            return this;
        }


        /**
         * Repository模板路径
         *
         * @return this
         * @since 3.5.6
         */
        public Repository.Builder repositoryTemplate(String template) {
            this.repository.repositoryTemplate = template;
            return this;
        }

        /**
         * RepositoryImpl模板路径
         *
         * @return this
         * @since 3.5.6
         */
        public Repository.Builder repositoryImplTemplate(String template) {
            this.repository.repositoryImplTemplate = template;
            return this;
        }

        public Repository get() {
            return this.repository;
        }
    }
}
