package com.lvwj.halo.embeddingmodel.bgem3;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.spi.model.embedding.EmbeddingModelFactory;

public class BgeM3QuantizedEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeM3QuantizedEmbeddingModel();
    }
}
