package com.lvwj.halo.tokenizer.config;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.lvwj.halo.tokenizer.core.Tokenizer;
import com.lvwj.halo.tokenizer.core.TokenizerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Tokenizer配置
 *
 * @author lvweijie
 * @date 2024年08月02日 15:16
 */
@AutoConfiguration
public class TokenizerConfiguration {

    public static final String QWEN_TOKENIZER = "qwenTokenizer";

    @Bean(QWEN_TOKENIZER)
    public Tokenizer qwenTokenizer() {
        return TokenizerFactory.qwen();
    }

    @Bean("lazyEncodingRegistry")
    @ConditionalOnMissingBean(EncodingRegistry.class)
    public EncodingRegistry lazyEncodingRegistry() {
        return Encodings.newLazyEncodingRegistry();
    }
}
