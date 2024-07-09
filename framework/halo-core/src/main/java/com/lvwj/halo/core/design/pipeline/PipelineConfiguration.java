package com.lvwj.halo.core.design.pipeline;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author lvweijie
 * @date 2023年11月22日 21:48
 */
@AutoConfiguration
public class PipelineConfiguration {

    @Bean
    public PipelineExecutor pipelineExecutor(){
        return new PipelineExecutor();
    }

    @Bean
    public PipelineRouter pipelineRouter(){
        return new PipelineRouter();
    }
}
