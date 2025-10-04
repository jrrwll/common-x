package org.dreamcat.common.spring.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.type.filter.AssignableTypeFilter;

/**
 * Create by tuke on 2020/3/27
 */
@SuppressWarnings({"unchecked"})
public final class SpringReflectUtil {

    private SpringReflectUtil() {
    }

    /**
     * get parameters' name of method
     *
     * @param method method
     * @return null if parameter name is discard in class file
     */
    public static String[] getParameterName(Method method) {
        ParameterNameDiscoverer parameterNameDiscoverer =
                new LocalVariableTableParameterNameDiscoverer();
        return parameterNameDiscoverer.getParameterNames(method);
    }

    public static <T> List<Class<? extends T>> getSubClasses(
            Class<T> clazz, String basePackage)
            throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(clazz));

        // scan package
        Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
        List<Class<? extends T>> list = new ArrayList<>(components.size());
        for (BeanDefinition component : components) {
            Class<? extends T> subClass = (Class<? extends T>) Class
                    .forName(component.getBeanClassName());
            list.add(subClass);
        }
        return list;
    }
}
