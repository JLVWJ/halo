package com.lvwj.halo.jieba;

import cn.hutool.extra.spring.SpringUtil;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 结巴分词
 *
 * @author lvweijie
 * @date 2024年10月11日 14:37
 */
public class Jieba {

    public static List<String> extractKeywords(String text) {
        return extractKeywords(text, null);
    }

    /**
     * 提取分词关键字
     *
     * @param text    原始文本
     * @param segMode segMode
     * @return java.util.List<java.lang.String>
     * @author lvweijie
     * @date 2024/10/11 15:05
     */
    public static List<String> extractKeywords(String text, JiebaSegmenter.SegMode segMode) {
        if (Func.isBlank(text))
            return Collections.emptyList();
        segMode = Optional.ofNullable(segMode).orElse(JiebaSegmenter.SegMode.INDEX);
        JiebaSegmenter jiebaSegmenter = SpringUtil.getBean(JiebaSegmenter.class);
        List<SegToken> tokens = jiebaSegmenter.process(StringUtil.removeSpecialCharacters(text), segMode);
        return tokens.stream().map(s -> s.word).collect(Collectors.toList());
    }

    public static Map<String, Integer> getKeywordFrequency(String text) {
        return getKeywordFrequency(text, null);
    }

    /**
     * 提取分词关键字，并获取词频
     *
     * @param text    原始文本
     * @param segMode SegMode
     * @return java.util.Map<java.lang.String, java.lang.Integer>
     * @author lvweijie
     * @date 2024/10/11 15:05
     */
    public static Map<String, Integer> getKeywordFrequency(String text, JiebaSegmenter.SegMode segMode) {
        List<String> keywords = extractKeywords(text, segMode);
        return getKeywordFrequency(keywords);
    }

    /**
     * 获取词频
     */
    public static Map<String, Integer> getKeywordFrequency(List<String> keywords) {
        Map<String, Integer> keywordFrequency = new HashMap<>();
        keywords.forEach(s -> {
            Integer freq = keywordFrequency.computeIfAbsent(s, k -> 0);
            freq++;
            keywordFrequency.put(s, freq);
        });
        return keywordFrequency;
    }
}
