package com.lvwj.halo.tokenizer.core;

/**
 * @author lvweijie
 * @date 2024年08月22日 11:24
 */
public final class TokenizerFactory {
    public static Tokenizer qwen() {
        return new QwenTokenizer();
    }
}
