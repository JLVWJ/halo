package com.lvwj.halo.tokenizer.core;

import cn.hutool.extra.spring.SpringUtil;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.tokenizer.config.TokenizerConfigration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author lvweijie
 * @date 2024年07月31日 11:20
 */
@Slf4j
@Component
public class TokenizerUtil {

    private static volatile Tokenizer qwenTokenizer;

    private static Tokenizer getQwenTokenizer() {
        if (null == qwenTokenizer) {
            synchronized (TokenizerUtil.class) {
                if (null == qwenTokenizer) {
                    qwenTokenizer = SpringUtil.getBean(TokenizerConfigration.QWEN_TOKENIZER);
                }
            }
        }
        return qwenTokenizer;
    }

    public static Integer count(String text) {
        return encode(text).size();
    }

    public static List<Integer> encode(String text) {
        if (Func.isBlank(text)) return Collections.emptyList();
        return getQwenTokenizer().encodeOrdinary(text);
    }

    public static String decode(List<Integer> list) {
        if (Func.isEmpty(list)) return null;
        return getQwenTokenizer().decode(list);
    }
}
