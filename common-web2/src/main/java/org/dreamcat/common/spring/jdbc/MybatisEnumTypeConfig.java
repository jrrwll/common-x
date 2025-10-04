package org.dreamcat.common.spring.jdbc;

import java.util.Objects;
import java.util.function.IntSupplier;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.dreamcat.common.mybatis.IntSupplierBatisTypeHandler;
import org.dreamcat.common.spring.ClassPathEnumScanner;
import org.dreamcat.common.util.ObjectUtil;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

/**
 * Create by tuke on 2021/5/6
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MybatisEnumTypeProperties.class)
public class MybatisEnumTypeConfig {

    @Bean
    @SuppressWarnings("unchecked")
    public ConfigurationCustomizer configreCustomizer(MybatisEnumTypeProperties intSupplierTypeProperties) {

        return configurationCustomizer -> {
            String[] enumBasePackages = intSupplierTypeProperties.getEnumBasePackages();
            if (ObjectUtil.isEmpty(enumBasePackages)) return;

            TypeHandlerRegistry typeHandlerRegistry = configurationCustomizer.getTypeHandlerRegistry();
            ClassPathEnumScanner classPathEnumScanner = new ClassPathEnumScanner();
            for (String enumBasePackage : enumBasePackages) {
                for (BeanDefinition beanDefinition : classPathEnumScanner
                        .findCandidateComponents(enumBasePackage)) {

                    Class<? extends IntSupplier> type = (Class<? extends IntSupplier>) ClassUtils
                            .resolveClassName(Objects.requireNonNull(beanDefinition.getBeanClassName()),
                                    ClassUtils.getDefaultClassLoader());
                    if (!type.isAnonymousClass() && type.isEnum()) {
                        typeHandlerRegistry.register(type, IntSupplierBatisTypeHandler.class);
                    }
                }
            }
        };
    }
}
