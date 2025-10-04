package org.dreamcat.common.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2021/4/12
 * <p>
 * https://www.baeldung.com/circular-dependencies-in-spring
 * https://www.baeldung.com/spring-boot-lazy-initialization
 * https://spring.io/blog/2019/03/14/lazy-initialization-in-spring-boot-2-2
 */
@Component
public class LazyBeansFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String name : beanFactory.getBeanDefinitionNames()) {
            beanFactory.getBeanDefinition(name).setLazyInit(true);
        }
    }
}
/*
spring:
  main:
    lazy-initialization: true

spring.main.lazy-initialization=true

SpringApplicationBuilder(Application.class)
  .lazyInitialization(true)
  .build(args)
  .run();

SpringApplication app = new SpringApplication(Application.class);
app.setLazyInitialization(true);
app.run(args);

 */
