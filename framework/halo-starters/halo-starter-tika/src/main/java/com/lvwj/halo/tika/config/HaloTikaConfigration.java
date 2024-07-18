package com.lvwj.halo.tika.config;

import com.lvwj.halo.tika.core.DocumentParser;
import com.lvwj.halo.tika.core.TikaDocumentParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author lvweijie
 * @date 2024年07月06日 15:10
 */
@AutoConfiguration
public class HaloTikaConfigration {

    @Bean
    @ConditionalOnMissingBean
    public DocumentParser documentParser(){return new TikaDocumentParser();}
}
