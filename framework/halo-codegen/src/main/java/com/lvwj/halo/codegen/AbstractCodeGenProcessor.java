package com.lvwj.halo.codegen;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 代码生成基类
 *
 * @author lvweijie
 * @date 2024/10/15 14:18
 */
abstract class AbstractCodeGenProcessor extends AbstractProcessor {
    private final TypeCollector typeCollector = new TypeCollector();

    abstract ProcessorPlugin[] getPlugins();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = Sets.newHashSet();
        for (ProcessorPlugin plugin : this.getPlugins()){
            for (Class<? extends Annotation> cls : plugin.applyAnnCls()) {
                types.add(cls.getName());
            }
        }
        return types;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        for (ProcessorPlugin plugin : this.getPlugins()){
            plugin.init(this.typeCollector);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (CollectionUtils.isEmpty(annotations)){
            return true;
        }
        typeCollector.syncForm(roundEnv);
        for (ProcessorPlugin plugin : this.getPlugins()) {
            try {
                System.out.printf("process by plugin %s%n", plugin.getClass());
                plugin.process(annotations, roundEnv);
                plugin.write(this.processingEnv.getFiler());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

}
