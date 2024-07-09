package com.lvwj.halo.lazyload.core;

import com.lvwj.halo.lazyload.LazyLoad;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 属性懒加载工厂
 *
 * @author lvweijie
 * @date 2023年11月04日 14:56
 */
public class PropertyLazyLoaderFactory {
    private final ApplicationContext applicationContext;

    public PropertyLazyLoaderFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public List<PropertyLazyLoader> createFor(Class<?> cls) {
        return FieldUtils.getAllFieldsList(cls).stream().map(this::createFromField).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private PropertyLazyLoader createFromField(Field field) {
        LazyLoad lazyLoad = AnnotatedElementUtils.findMergedAnnotation(field, LazyLoad.class);
        if (lazyLoad == null) {
            return null;
        }

        String targetEl = lazyLoad.value();
        Annotation[] annotations = field.getAnnotations();
        for (Annotation anno : annotations) {
            AnnotationAttributes annotationAttributes = AnnotatedElementUtils.findMergedAnnotationAttributes(field, anno.annotationType(), true, true);
            if (null == annotationAttributes) {
                continue;
            }
            for (Map.Entry<String, Object> entry : annotationAttributes.entrySet()) {
                String key = "${" + entry.getKey() + "}";
                String value = String.valueOf(entry.getValue());
                targetEl = targetEl.replace(key, value);
            }
        }

        return new PropertyLazyLoader(field, new BeanFactoryResolver(this.applicationContext), targetEl);
    }
}
