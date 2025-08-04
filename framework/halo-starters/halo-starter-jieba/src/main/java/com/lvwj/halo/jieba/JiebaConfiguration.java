package com.lvwj.halo.jieba;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 结巴分词
 *
 * @author lvweijie
 * @date 2024年10月12日 15:53
 */
@AutoConfiguration
public class JiebaConfiguration {

    @Bean
    public JiebaSegmenter jiebaSegmenter(){
        return new JiebaSegmenter();
    }
}
