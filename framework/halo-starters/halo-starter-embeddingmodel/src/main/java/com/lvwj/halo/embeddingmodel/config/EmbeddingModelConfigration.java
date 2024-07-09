package com.lvwj.halo.embeddingmodel.config;

import com.lvwj.halo.embeddingmodel.bgem3.BgeM3QuantizedEmbeddingModelFactory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author lvweijie
 * @date 2024年07月06日 15:10
 */
@AutoConfiguration
public class EmbeddingModelConfigration {

    @Bean
    public EmbeddingModel embeddingModel(){return new BgeM3QuantizedEmbeddingModelFactory().create();
    }
}
