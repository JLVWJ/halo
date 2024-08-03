package com.lvwj.halo.elasticsearch.config;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.core.config.GlobalConfig;
import org.dromara.easyes.core.kernel.BaseEsMapper;
import org.dromara.easyes.starter.config.EasyEsConfigProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;


@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(HaloElasticProperties.class)
public class HaloElasticConfigration {

    @Resource
    private HaloElasticProperties properties;

    @Resource
    private EasyEsConfigProperties easyEsConfigProperties;

    @Bean
    public CommandLineRunner autoCreateIndex() {
        return args -> {
            if (!properties.getAutoIndex() || !easyEsConfigProperties.isEnable()) return;
            Map<String, BaseEsMapper> beansOfType = SpringUtil.getBeansOfType(BaseEsMapper.class);
            if (CollectionUtils.isEmpty(beansOfType)) return;
            for (Map.Entry<String, BaseEsMapper> entry : beansOfType.entrySet()) {
                Class entityClass = entry.getValue().getEntityClass();
                IndexName indexName = AnnotatedElementUtils.findMergedAnnotation(entityClass, IndexName.class);
                if (null == indexName) {
                    throw new RuntimeException(String.format("es doc entity[%s] has no annotation(@IndexName), please check it!", entityClass.getSimpleName()));
                }
                String idxName = indexName.value();
                if (!StringUtils.hasLength(idxName)) {
                    throw new RuntimeException(String.format("es doc entity[%s]'s annotation(@IndexName) value is empty, please check it!", entityClass.getSimpleName()));
                }
                String idxPrefix = Optional.ofNullable(easyEsConfigProperties.getGlobalConfig())
                        .map(GlobalConfig::getDbConfig)
                        .map(GlobalConfig.DbConfig::getIndexPrefix)
                        .orElse("");
                idxName = indexName.keepGlobalPrefix() ? idxPrefix + idxName : idxName;
                try {
                    if (!entry.getValue().existsIndex(idxName)) {
                        entry.getValue().createIndex();
                    }
                } catch (Exception e) {
                    String error = String.format("es doc entity[%s] auto create index[%s] failed, please check it!", entityClass.getSimpleName(), idxName);
                    log.error(error, e);
                    throw new RuntimeException(error);
                }
            }
        };
    }
}
