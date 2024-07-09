package com.lvwj.halo.join.support;

import com.lvwj.halo.join.JoinInMemory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
@Slf4j
public class JoinInMemoryBasedJoinItemExecutorFactory extends AbstractJoinItemExecutorFactory<JoinInMemory> {
    private final ExpressionParser parser = new SpelExpressionParser();
    private final TemplateParserContext templateParserContext = new TemplateParserContext();
    private final BeanResolver beanResolver;

    public JoinInMemoryBasedJoinItemExecutorFactory(BeanResolver beanResolver) {
        super(JoinInMemory.class);
        this.beanResolver = beanResolver;
    }


    @Override
    protected BiConsumer<Object, List<Object>> createFoundFunction(Class<?> cls, Field field, JoinInMemory ann) {
        log.info("write field is {} for class {}", field.getName(), cls);
        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        return new DataSetter(field.getName(), isCollection);
    }

    @Override
    protected Function<Object, Object> createJoinDataConverter(Class<?> cls, Field field, JoinInMemory ann) {
        if (StringUtils.isEmpty(ann.joinDataConverter())) {
            log.info("No Data Convert for class {}, field {}", cls, field.getName());
            return Function.identity();
        } else {
            log.info("Data Convert is {} for class {}, field {}", ann.joinDataConverter(), cls, field.getName());
            return new DataGetter(ann.joinDataConverter());
        }
    }

    @Override
    protected Function<Object, Object> createKeyFromJoinData(Class<?> cls, Field field, JoinInMemory ann) {
        log.info("Key from join data is {} for class {}, field {}",
                ann.keyFromJoinData(), cls, field.getName());
        return new DataGetter(ann.keyFromJoinData());
    }

    @Override
    protected Function<List<Object>, List<Object>> createJoinDataLoader(Class<?> cls, Field field, JoinInMemory ann) {
        log.info("data loader is {} for class {}, field {}",
                ann.joinDataLoader(), cls, field.getName());
        return new DataGetter(ann.joinDataLoader());
    }

    @Override
    protected Function<Object, Object> createKeyFromSourceData(Class<?> cls, Field field, JoinInMemory ann) {
        log.info("Key from source data is {} for class {}, field {}",
                ann.keyFromJoinData(), cls, field.getName());
        return new DataGetter(ann.keyFromSourceData());
    }

    @Override
    protected int createRunLevel(Class<?> cls, Field field, JoinInMemory ann) {
        log.info("run level is {} for class {}, field {}",
                ann.runLevel(), cls, field.getName());
        return ann.runLevel();
    }

    private class DataSetter implements BiConsumer<Object, List<Object>> {
        private final String fieldName;
        private final boolean isCollection;
        private final Expression expression;

        private DataSetter(String fieldName, boolean isCollection) {
            this.fieldName = fieldName;
            this.expression = parser.parseExpression(fieldName);
            this.isCollection = isCollection;
        }

        @Override
        public void accept(Object data, List<Object> result) {
            if (isCollection) {
                this.expression.setValue(data, result);
            } else {
                int size = result.size();
                if (size == 1) {
                    this.expression.setValue(data, result.get(0));
                } else {
                    log.warn("write join result to {} error, field is {}, data is {}", data, fieldName, result);
                }
            }
        }
    }

    private class DataGetter<T, R> implements Function<T, R> {
        private final String expStr;
        private final Expression expression;
        private final EvaluationContext evaluationContext;

        private DataGetter(String expStr) {
            this.expStr = expStr;
            this.expression = parser.parseExpression(expStr, templateParserContext);
            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.setBeanResolver(beanResolver);
            this.evaluationContext = evaluationContext;
        }

        @Override
        public Object apply(Object data) {
            if (data == null) {
                return null;
            }

            return expression.getValue(evaluationContext, data);
        }
    }
}
