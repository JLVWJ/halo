package com.lvwj.codegen.templateengine.engine;

import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.util.ClassUtils;
import com.baomidou.mybatisplus.generator.util.FileUtils;
import com.baomidou.mybatisplus.generator.util.RuntimeUtils;
import com.lvwj.codegen.templateengine.config.ConfigBuilder;
import com.lvwj.codegen.templateengine.config.DomainModelType;
import com.lvwj.codegen.templateengine.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * @author lvweijie
 * @date 2024年11月06日 13:55
 */
@Slf4j
public abstract class AbstractTemplateEnginePlus {

    /**
     * 配置信息
     */
    private ConfigBuilder configBuilder;

    /**
     * 模板引擎初始化
     */

    public abstract AbstractTemplateEnginePlus init(ConfigBuilder configBuilder);

    /**
     * 输出自定义模板文件
     *
     * @param customFiles 自定义模板文件列表
     * @param tableInfo   表信息
     * @param objectMap   渲染数据
     * @since 3.5.3
     */
    protected void outputCustomFile(List<CustomFile> customFiles, TableInfo tableInfo, Map<String, Object> objectMap) {
        String entityName = tableInfo.getEntityName();
        String parentPath = getPathInfo(OutputFile.parent);
        customFiles.forEach(file -> {
            String filePath = StringUtils.isNotBlank(file.getFilePath()) ? file.getFilePath() : parentPath;
            if (StringUtils.isNotBlank(file.getPackageName())) {
                filePath = filePath + File.separator + file.getPackageName().replaceAll("\\.", "\\" + File.separator);
            }
            Function<TableInfo, String> formatNameFunction = file.getFormatNameFunction();
            //生成带apiType的文件
            if (fileNames.contains(file.getFileName())) {
                List<String> apiTypes = (List<String>) objectMap.get("apiTypes");
                for (String apiType : apiTypes) {
                    objectMap.put("apiType", apiType);
                    String fileName = filePath + File.separator + apiType.toLowerCase() + File.separator + (null != formatNameFunction ? formatNameFunction.apply(tableInfo) : entityName) + apiType + file.getFileName();
                    outputFile(new File(fileName), objectMap, file.getTemplatePath(), file.isFileOverride());
                }
            } else {
                String fileName = filePath + File.separator + (null != formatNameFunction ? formatNameFunction.apply(tableInfo) : entityName) + file.getFileName();
                outputFile(new File(fileName), objectMap, file.getTemplatePath(), file.isFileOverride());
            }
        });
    }

    /**
     * 输出实体文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputEntity(TableInfo tableInfo, Map<String, Object> objectMap) {
        String entityName = tableInfo.getEntityName();
        String entityPath = getPathInfo(OutputFile.entity);
        Entity entity = this.getConfigPlusBuilder().getStrategyConfig().entity();
        GlobalConfig globalConfig = configBuilder.getGlobalConfig();
        if (entity.isGenerate()) {
            String entityFile = String.format((entityPath + File.separator + "%s" + suffixJavaOrKt()), entityName);
            outputFile(getOutputFile(entityFile, OutputFile.entity), objectMap, templateFilePath(globalConfig.isKotlin() ? entity.getKotlinTemplate() : entity.getJavaTemplate()), getConfigPlusBuilder().getStrategyConfig().entity().isFileOverride());
        }
    }

    protected File getOutputFile(String filePath, OutputFile outputFile) {
        return getConfigPlusBuilder().getStrategyConfig().getOutputFile().createFile(filePath, outputFile);
    }

    /**
     * 输出Mapper文件(含xml)
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputMapper(TableInfo tableInfo, Map<String, Object> objectMap) {
        // MpMapper.java
        String entityName = tableInfo.getEntityName();
        String mapperPath = getPathInfo(OutputFile.mapper);
        Mapper mapper = this.getConfigPlusBuilder().getStrategyConfig().mapper();
        if (mapper.isGenerateMapper()) {
            String mapperFile = String.format((mapperPath + File.separator + tableInfo.getMapperName() + suffixJavaOrKt()), entityName);
            outputFile(getOutputFile(mapperFile, OutputFile.mapper), objectMap, templateFilePath(mapper.getMapperTemplatePath()), getConfigPlusBuilder().getStrategyConfig().mapper().isFileOverride());
        }
        // MpMapper.xml
        String xmlPath = getPathInfo(OutputFile.xml);
        if (mapper.isGenerateMapperXml()) {
            String xmlFile = String.format((xmlPath + File.separator + tableInfo.getXmlName() + ConstVal.XML_SUFFIX), entityName);
            outputFile(getOutputFile(xmlFile, OutputFile.xml), objectMap, templateFilePath(mapper.getMapperXmlTemplatePath()), getConfigPlusBuilder().getStrategyConfig().mapper().isFileOverride());
        }
    }

    /**
     * 输出service文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputService(TableInfo tableInfo, Map<String, Object> objectMap) {
        // IMpService.java
        String entityName = tableInfo.getEntityName();
        // 判断是否要生成service接口
        Service service = this.getConfigPlusBuilder().getStrategyConfig().service();
        boolean generateService = Boolean.parseBoolean(objectMap.get("generateService").toString());
        if (generateService) {
            String servicePath = getPathInfo(OutputFile.service);
            String serviceFile = String.format((servicePath + File.separator + tableInfo.getServiceName() + suffixJavaOrKt()), entityName);
            outputFile(getOutputFile(serviceFile, OutputFile.service), objectMap, templateFilePath(service.getServiceTemplate()), getConfigPlusBuilder().getStrategyConfig().service().isFileOverride());
        }
        // MpServiceImpl.java
        boolean generateServiceImpl = Boolean.parseBoolean(objectMap.get("generateServiceImpl").toString());
        if (generateServiceImpl) {
            String serviceImplPath = getPathInfo(OutputFile.serviceImpl);
            String implFile = String.format((serviceImplPath + File.separator + tableInfo.getServiceImplName() + suffixJavaOrKt()), entityName);
            outputFile(getOutputFile(implFile, OutputFile.serviceImpl), objectMap, templateFilePath(service.getServiceImplTemplate()), getConfigPlusBuilder().getStrategyConfig().service().isFileOverride());
        }
    }

    /**
     * 输出controller文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputController(TableInfo tableInfo, Map<String, Object> objectMap) {
        // MpController.java
        Controller controller = this.getConfigPlusBuilder().getStrategyConfig().controller();
        String controllerPath = getPathInfo(OutputFile.controller);
        if (controller.isGenerate()) {
            String entityName = tableInfo.getEntityName();
            String controllerFile = String.format((controllerPath + File.separator + tableInfo.getControllerName() + suffixJavaOrKt()), entityName);
            outputFile(getOutputFile(controllerFile, OutputFile.controller), objectMap, templateFilePath(controller.getTemplatePath()), getConfigPlusBuilder().getStrategyConfig().controller().isFileOverride());
        }
    }

    /**
     * 输出文件
     *
     * @param file         文件
     * @param objectMap    渲染信息
     * @param templatePath 模板路径
     * @param fileOverride 是否覆盖已有文件
     * @since 3.5.2
     */
    protected void outputFile(File file, Map<String, Object> objectMap, String templatePath, boolean fileOverride) {
        if (isCreate(file, fileOverride)) {
            try {
                // 全局判断【默认】
                boolean exist = file.exists();
                if (!exist) {
                    File parentFile = file.getParentFile();
                    FileUtils.forceMkdir(parentFile);
                }
                writer(objectMap, templatePath, file);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     * 获取路径信息
     *
     * @param outputFile 输出文件
     * @return 路径信息
     */

    protected String getPathInfo(OutputFile outputFile) {
        return getConfigPlusBuilder().getPathInfo().get(outputFile);
    }

    /**
     * 批量输出 java xml 文件
     */

    public AbstractTemplateEnginePlus batchOutput() {
        try {
            ConfigBuilder config = this.getConfigPlusBuilder();
            Map<DomainModelType, List<CustomFile>> customFiles = getCustomFiles(config.getStrategyConfig());
            List<TableInfo> tableInfoList = config.getTableInfoList();
            tableInfoList.forEach(tableInfo -> {
                Map<String, Object> objectMap = this.getObjectMap(config, tableInfo);
                Optional.ofNullable(config.getInjectionConfig()).ifPresent(t -> {
                    // 添加自定义属性
                    t.beforeOutputFile(tableInfo, objectMap);
                    // 输出自定义文件
                    outputCustomFile(getCustomFiles(config, tableInfo, customFiles), tableInfo, objectMap);
                });
                // entity
                outputEntity(tableInfo, objectMap);
                // mapper and xml
                outputMapper(tableInfo, objectMap);
                // service
                outputService(tableInfo, objectMap);
                // controller
                outputController(tableInfo, objectMap);
            });
        } catch (Exception e) {
            throw new RuntimeException("无法创建文件，请检查配置信息！", e);
        }
        return this;
    }

    /**
     * 将模板转化成为文件
     *
     * @param objectMap    渲染对象 MAP 信息
     * @param templatePath 模板文件
     * @param outputFile   文件生成的目录
     * @throws Exception 异常
     * @since 3.5.0
     */
    public abstract void writer(Map<String, Object> objectMap, String templatePath, File outputFile) throws Exception;

    /**
     * 打开输出目录
     */
    public void open() {
        String outDir = getConfigPlusBuilder().getGlobalConfig().getOutputDir();
        if (StringUtils.isBlank(outDir) || !new File(outDir).exists()) {
            System.err.println("未找到输出目录：" + outDir);
        } else if (getConfigPlusBuilder().getGlobalConfig().isOpen()) {
            try {
                RuntimeUtils.openDir(outDir);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 渲染对象 MAP 信息
     *
     * @param config    配置信息
     * @param tableInfo 表信息对象
     * @return ignore
     */

    public Map<String, Object> getObjectMap(ConfigBuilder config, TableInfo tableInfo) {
        StrategyConfig strategyConfig = config.getStrategyConfig();
        Map<String, Object> controllerData = strategyConfig.controller().renderData(tableInfo);
        Map<String, Object> objectMap = new HashMap<>(controllerData);
        Map<String, Object> mapperData = strategyConfig.mapper().renderData(tableInfo);
        objectMap.putAll(mapperData);
        Map<String, Object> serviceData = strategyConfig.service().renderData(tableInfo);
        objectMap.putAll(serviceData);
        Map<String, Object> entityData = strategyConfig.entity().renderData(tableInfo);
        objectMap.putAll(entityData);
        objectMap.put("config", config);
        objectMap.put("package", getPackageInfo(config));
        GlobalConfig globalConfig = config.getGlobalConfig();
        objectMap.put("author", globalConfig.getAuthor());
        objectMap.put("kotlin", globalConfig.isKotlin());
        objectMap.put("swagger", globalConfig.isSwagger());
        objectMap.put("springdoc", globalConfig.isSpringdoc());
        objectMap.put("date", globalConfig.getCommentDate());
        // 启用 schema 处理逻辑
        String schemaName = "";
        if (strategyConfig.isEnableSchema()) {
            // 存在 schemaName 设置拼接 . 组合表名
            schemaName = config.getDataSourceConfig().getSchemaName();
            if (StringUtils.isNotBlank(schemaName)) {
                schemaName += ".";
                tableInfo.setConvert(true);
            }
        }
        objectMap.put("schemaName", schemaName);
        objectMap.put("table", tableInfo);
        objectMap.put("entity", tableInfo.getEntityName());
        objectMap.put("aggregate", getEntityName(tableInfo.getName(), config.getStrategyConfig()));
        addGlobalConfigToObjectMap(objectMap, globalConfig);
        return objectMap;
    }

    /**
     * 模板真实文件路径
     *
     * @param filePath 文件路径
     * @return ignore
     */

    public abstract String templateFilePath(String filePath);

    /**
     * 检查文件是否创建文件
     *
     * @param file         文件
     * @param fileOverride 是否覆盖已有文件
     * @return 是否创建文件
     * @since 3.5.2
     */
    protected boolean isCreate(File file, boolean fileOverride) {
        if (file.exists() && !fileOverride) {
            log.warn("文件[{}]已存在，且未开启文件覆盖配置，需要开启配置可到策略配置中设置！！！", file.getName());
        }
        return !file.exists() || fileOverride;
    }

    /**
     * 文件后缀
     */
    protected String suffixJavaOrKt() {
        return getConfigPlusBuilder().getGlobalConfig().isKotlin() ? ConstVal.KT_SUFFIX : ConstVal.JAVA_SUFFIX;
    }


    public ConfigBuilder getConfigPlusBuilder() {
        return configBuilder;
    }


    public AbstractTemplateEnginePlus setConfigPlusBuilder(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        return this;
    }

    private List<CustomFile> getCustomFiles(ConfigBuilder config, TableInfo tableInfo, Map<DomainModelType, List<CustomFile>> customFiles) {
        InjectionConfig t = config.getInjectionConfig();
        List<CustomFile> customFileList = new ArrayList<>(t.getCustomFiles());
        if (config.getGlobalConfig().isDdd()) {
            Map<String, DomainModelType> map = (Map<String, DomainModelType>) t.getCustomMap().get(DomainModelType.NAME);
            if (null == map)
                throw new RuntimeException("DDD模式下，需配置相关表的领域模型类型(DomainModelType)。配置路径: InjectionConfig.CustomMap.put(DomainModelType.NAME,{'tb_name1':DomainModelType.aggregate,'tb_name2':DomainModelType.entity})");
            DomainModelType modelType = map.getOrDefault(tableInfo.getName(), DomainModelType.aggregate);
            customFileList.addAll(customFiles.get(modelType));
        }
        return customFileList;
    }

    private void addGlobalConfigToObjectMap(Map<String, Object> objectMap, GlobalConfig globalConfig) {
        objectMap.put("dubbo", globalConfig.isDubbo());
        objectMap.put("feign", globalConfig.isFeign());
        objectMap.put("appName", globalConfig.getAppName());
        objectMap.put("apiTypes", globalConfig.getApiTypes());
        objectMap.put("responseWrapperClassPackage", globalConfig.getResponseWrapperClass());
        objectMap.put("responseWrapperClass", ClassUtils.getSimpleName(globalConfig.getResponseWrapperClass()));
        if (globalConfig.isDdd()) {
            objectMap.put("generateService", false);
            objectMap.put("generateServiceImpl", true);

            objectMap.put("superAggregateClassPackage", globalConfig.getSuperAggregateClass());
            objectMap.put("superAggregateClass", ClassUtils.getSimpleName(globalConfig.getSuperAggregateClass()));
            objectMap.put("superDomainEntityClassPackage", globalConfig.getSuperDomainEntityClass());
            objectMap.put("superDomainEntityClass", ClassUtils.getSimpleName(globalConfig.getSuperDomainEntityClass()));
            objectMap.put("superValueObjClassPackage", globalConfig.getSuperValueObjClass());
            objectMap.put("superValueObjClass", ClassUtils.getSimpleName(globalConfig.getSuperValueObjClass()));
            objectMap.put("superDomainEventClassPackage", globalConfig.getSuperDomainEventClass());
            objectMap.put("superDomainEventClass", ClassUtils.getSimpleName(globalConfig.getSuperDomainEventClass()));
            objectMap.put("superIntegrationEventClassPackage", globalConfig.getSuperIntegrationEventClass());
            objectMap.put("superIntegrationEventClass", ClassUtils.getSimpleName(globalConfig.getSuperIntegrationEventClass()));
            objectMap.put("superConverterClassPackage", globalConfig.getSuperConverterClass());
            objectMap.put("superConverterClass", ClassUtils.getSimpleName(globalConfig.getSuperConverterClass()));
            objectMap.put("superRepositoryClassPackage", globalConfig.getSuperRepositoryClass());
            objectMap.put("superRepositoryClass", ClassUtils.getSimpleName(globalConfig.getSuperRepositoryClass()));
            objectMap.put("superRepositoryImplClassPackage", globalConfig.getSuperRepositoryImplClass());
            objectMap.put("superRepositoryImplClass", ClassUtils.getSimpleName(globalConfig.getSuperRepositoryImplClass()));
        }
    }

    private Map<String,String> getPackageInfo(ConfigBuilder config) {
        PackageConfig packageConfig = config.getPackageConfig();
        Map<String, String> packageInfo = new HashMap<>(packageConfig.getPackageInfo());
        if (config.getGlobalConfig().isDdd()) {
            packageInfo.put("Aggregate", packageConfig.joinPackage(packageNames.get("Aggregate")));
            packageInfo.put("DomainEntity", packageConfig.joinPackage(packageNames.get("DomainEntity")));
            packageInfo.put("ValueObj", packageConfig.joinPackage(packageNames.get("ValueObj")));
            packageInfo.put("Command", packageConfig.joinPackage(packageNames.get("Command")));
            packageInfo.put("DomainEvent", packageConfig.joinPackage(packageNames.get("DomainEvent")));
            packageInfo.put("IntegrationEvent", packageConfig.joinPackage(packageNames.get("IntegrationEvent")));
            packageInfo.put("Repository", packageConfig.joinPackage(packageNames.get("Repository")));
            packageInfo.put("Request", packageConfig.joinPackage(packageNames.get("Request")));
            packageInfo.put("Response", packageConfig.joinPackage(packageNames.get("Response")));
            packageInfo.put("Converter", packageConfig.joinPackage(packageNames.get("Converter")));
            packageInfo.put("Assembler", packageConfig.joinPackage(packageNames.get("Assembler")));
            packageInfo.put("RepositoryImpl", packageConfig.joinPackage(packageNames.get("RepositoryImpl")));
            packageInfo.put("ApplicationService", packageConfig.joinPackage(packageNames.get("ApplicationService")));
            packageInfo.put("Facade", packageConfig.joinPackage(packageNames.get("Facade")));
            packageInfo.put("FacadeImpl", packageConfig.joinPackage(packageNames.get("FacadeImpl")));
        }
        packageInfo.put(ConstVal.ENTITY, packageConfig.joinPackage("infrastructure.persistence.entity"));
        packageInfo.put(ConstVal.MAPPER, packageConfig.joinPackage("infrastructure.persistence.mapper"));
        packageInfo.put(ConstVal.XML, packageConfig.joinPackage("infrastructure.persistence.mapper.xml"));
        packageInfo.put(ConstVal.SERVICE, packageConfig.joinPackage("infrastructure.persistence.service"));
        packageInfo.put(ConstVal.SERVICE_IMPL, packageConfig.joinPackage("infrastructure.persistence.service"));
        packageInfo.put(ConstVal.CONTROLLER, packageConfig.joinPackage("interfaces.controller"));
        return packageInfo;
    }

    private static final Map<String,String> packageNames = new HashMap<>(){
        {put("Aggregate","domain.aggregate");}
        {put("DomainEntity","domain.entity");}
        {put("ValueObj","domain.valobj");}
        {put("Command","domain.cmd");}
        {put("DomainEvent","domain.event");}
        {put("IntegrationEvent","domain.event");}
        {put("Repository","domain.repository");}
        {put("Request","api.dto.req");}
        {put("Response","api.dto.resp");}
        {put("Converter","infrastructure.converter");}
        {put("RepositoryImpl","infrastructure.repository.impl");}
        {put("ApplicationService","service");}
        {put("Assembler","interfaces.assembler");}
        {put("Facade","api.facade");}
        {put("FacadeImpl","interfaces.facade.impl");}
    };

    private static Map<DomainModelType, List<CustomFile>> getCustomFiles(StrategyConfig strategyConfig) {
        String templates = "/templates/";
        CustomFile aggregateFile = new CustomFile.Builder().packageName(packageNames.get("Aggregate"))
                .templatePath(templates + "aggregate.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName(".java")
                .enableFileOverride().build();
        CustomFile aggregateCreatedEventFile = new CustomFile.Builder().packageName(packageNames.get("DomainEvent"))
                .templatePath(templates + "aggregateCreatedEvent.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("CreatedEvent.java")
                .enableFileOverride().build();
        CustomFile aggregateCreatedIntegrationEventFile = new CustomFile.Builder().packageName(packageNames.get("IntegrationEvent"))
                .templatePath(templates + "aggregateCreatedIntegrationEvent.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("CreatedIntegrationEvent.java")
                .enableFileOverride().build();
        CustomFile aggregateSaveCmdFile = new CustomFile.Builder().packageName(packageNames.get("Command"))
                .templatePath(templates + "aggregateSaveCmd.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("SaveCmd.java")
                .enableFileOverride().build();
        CustomFile aggregateCreateReqFile = new CustomFile.Builder().packageName(packageNames.get("Request"))
                .templatePath(templates + "aggregateCreateReq.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig) + "Create")
                .fileName("Req.java")
                .enableFileOverride().build();
        CustomFile aggregateUpdateReqFile = new CustomFile.Builder().packageName(packageNames.get("Request"))
                .templatePath(templates + "aggregateUpdateReq.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig) + "Update")
                .fileName("Req.java")
                .enableFileOverride().build();
        CustomFile aggregateRespFile = new CustomFile.Builder().packageName(packageNames.get("Response"))
                .templatePath(templates + "aggregateResp.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("Resp.java")
                .enableFileOverride().build();
        CustomFile converterFile = new CustomFile.Builder().packageName(packageNames.get("Converter"))
                .templatePath(templates + "converter.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("Converter.java")
                .enableFileOverride().build();
        CustomFile repositoryFile = new CustomFile.Builder().packageName(packageNames.get("Repository"))
                .templatePath(templates + "repository.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("Repository.java")
                .enableFileOverride().build();
        CustomFile repositoryImplFile = new CustomFile.Builder().packageName(packageNames.get("RepositoryImpl"))
                .templatePath(templates + "repositoryImpl.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("RepositoryImpl.java")
                .enableFileOverride().build();
        CustomFile cmdServiceFile = new CustomFile.Builder().packageName(packageNames.get("ApplicationService"))
                .templatePath(templates + "aggregateCmdService.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("CmdService.java")
                .enableFileOverride().build();
        CustomFile qryServiceFile = new CustomFile.Builder().packageName(packageNames.get("ApplicationService"))
                .templatePath(templates + "aggregateQryService.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("QryService.java")
                .enableFileOverride().build();
        CustomFile factoryFile = new CustomFile.Builder().packageName(packageNames.get("Aggregate"))
                .templatePath(templates + "aggregateFactory.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("Factory.java")
                .enableFileOverride().build();
        CustomFile assemblerFile = new CustomFile.Builder().packageName(packageNames.get("Assembler"))
                .templatePath(templates + "assembler.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("Assembler.java")
                .enableFileOverride().build();
        CustomFile facadeFile = new CustomFile.Builder().packageName(packageNames.get("Facade"))
                .templatePath(templates + "facade.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("Facade.java")
                .enableFileOverride().build();
        CustomFile facadeImplFile = new CustomFile.Builder().packageName(packageNames.get("FacadeImpl"))
                .templatePath(templates + "facadeImpl.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName("FacadeImpl.java")
                .enableFileOverride().build();

        CustomFile domainEntityFile = new CustomFile.Builder().packageName(packageNames.get("DomainEntity"))
                .templatePath(templates + "domainEntity.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName(".java")
                .enableFileOverride().build();

        CustomFile valueObjFile = new CustomFile.Builder().packageName(packageNames.get("ValueObj"))
                .templatePath(templates + "valueObj.java.vm")
                .formatNameFunction(t -> getEntityName(t.getName(), strategyConfig))
                .fileName(".java")
                .enableFileOverride().build();
        Map<DomainModelType, List<CustomFile>> result = new HashMap<>(2);
        result.put(DomainModelType.aggregate, Arrays.asList(aggregateFile, aggregateSaveCmdFile, aggregateCreatedEventFile, aggregateCreatedIntegrationEventFile, aggregateCreateReqFile, aggregateUpdateReqFile, aggregateRespFile,
                repositoryFile, repositoryImplFile, converterFile, cmdServiceFile, qryServiceFile, factoryFile, assemblerFile, facadeFile, facadeImplFile));
        result.put(DomainModelType.entity, Collections.singletonList(domainEntityFile));
        result.put(DomainModelType.valveObj, Collections.singletonList(valueObjFile));
        return result;
    }

    private static String getEntityName(String name, StrategyConfig strategyConfig) {
        return NamingStrategy.capitalFirst(processName(name, strategyConfig.entity().getNaming(), strategyConfig.getTablePrefix(), strategyConfig.getTableSuffix()));
    }

    private static String processName(String name, NamingStrategy strategy, Set<String> prefix, Set<String> suffix) {
        String propertyName = name;
        // 删除前缀
        if (!prefix.isEmpty()) {
            propertyName = NamingStrategy.removePrefix(propertyName, prefix);
        }
        // 删除后缀
        if (!suffix.isEmpty()) {
            propertyName = NamingStrategy.removeSuffix(propertyName, suffix);
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new RuntimeException(String.format("%s 的名称转换结果为空，请检查是否配置问题", name));
        }
        // 下划线转驼峰
        if (NamingStrategy.underline_to_camel.equals(strategy)) {
            return NamingStrategy.underlineToCamel(propertyName);
        }
        return propertyName;
    }

    private static final List<String> fileNames = Arrays.asList("Facade.java","FacadeImpl.java","Req.java","Resp.java");
}
