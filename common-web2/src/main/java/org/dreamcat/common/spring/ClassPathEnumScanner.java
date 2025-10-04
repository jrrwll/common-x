package org.dreamcat.common.spring;

import java.util.function.IntSupplier;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2021/5/6
 */
@Component
public class ClassPathEnumScanner extends ClassPathScanningCandidateComponentProvider {

    public ClassPathEnumScanner() {
        super(false);
        addIncludeFilter(new AssignableTypeFilter(IntSupplier.class));
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent();
    }
}
