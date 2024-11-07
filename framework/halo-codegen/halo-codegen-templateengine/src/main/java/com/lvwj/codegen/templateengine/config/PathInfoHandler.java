package com.lvwj.codegen.templateengine.config;

import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.Controller;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.builder.Mapper;
import com.baomidou.mybatisplus.generator.config.builder.Service;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvweijie
 * @date 2024年11月06日 14:14
 */
public class PathInfoHandler {

    @Getter
    private final Map<OutputFile, String> pathInfo = new HashMap<>();

    /**
     * 输出目录
     */
    private final String outputDir;

    /**
     * 包配置信息
     */
    private final PackageConfig packageConfig;

    PathInfoHandler(GlobalConfigPlus globalConfig, StrategyConfig strategyConfig, PackageConfig packageConfig) {
        this.outputDir = globalConfig.getOutputDir();
        this.packageConfig = packageConfig;
        // 设置默认输出路径
        this.setDefaultPathInfo(globalConfig, strategyConfig);
        // 覆盖自定义路径
        Map<OutputFile, String> pathInfo = packageConfig.getPathInfo();
        if (null != pathInfo && !pathInfo.isEmpty()) {
            this.pathInfo.putAll(pathInfo);
        }
    }

    /**
     * 设置默认输出路径
     *
     * @param globalConfig   全局配置
     * @param strategyConfig 模板配置
     */
    private void setDefaultPathInfo(GlobalConfigPlus globalConfig, StrategyConfig strategyConfig) {
        Entity entity = strategyConfig.entity();
        if (entity.isGenerate()) {
            putPathInfo(globalConfig.isKotlin() ? entity.getKotlinTemplate() : entity.getJavaTemplate(), OutputFile.entity, ConstVal.ENTITY);
        }
        Mapper mapper = strategyConfig.mapper();
        if (mapper.isGenerateMapper()) {
            putPathInfo(mapper.getMapperTemplatePath(), OutputFile.mapper, ConstVal.MAPPER);
        }
        if (mapper.isGenerateMapperXml()) {
            putPathInfo(mapper.getMapperXmlTemplatePath(), OutputFile.xml, ConstVal.XML);
        }
        Service service = strategyConfig.service();
        if (service.isGenerateService()) {
            putPathInfo(service.getServiceTemplate(), OutputFile.service, ConstVal.SERVICE);
        }
        if (service.isGenerateServiceImpl()) {
            putPathInfo(service.getServiceImplTemplate(), OutputFile.serviceImpl, ConstVal.SERVICE_IMPL);
        }
        Controller controller = strategyConfig.controller();
        if (controller.isGenerate()) {
            putPathInfo(controller.getTemplatePath(), OutputFile.controller, ConstVal.CONTROLLER);
        }
        putPathInfo(OutputFile.parent, ConstVal.PARENT);
    }

    private void putPathInfo(String template, OutputFile outputFile, String module) {
        if (StringUtils.isNotBlank(template)) {
            putPathInfo(outputFile, module);
        }
    }

    private void putPathInfo(OutputFile outputFile, String module) {
        pathInfo.putIfAbsent(outputFile, joinPath(outputDir, packageConfig.getPackageInfo(module)));
    }

    /**
     * 连接路径字符串
     *
     * @param parentDir   路径常量字符串
     * @param packageName 包名
     * @return 连接后的路径
     */
    private String joinPath(String parentDir, String packageName) {
        if (StringUtils.isBlank(parentDir)) {
            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        packageName = packageName.replaceAll("\\.", "\\" + File.separator);
        return parentDir + packageName;
    }
}
