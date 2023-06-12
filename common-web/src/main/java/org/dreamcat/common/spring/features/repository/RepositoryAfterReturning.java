package org.dreamcat.common.spring.features.repository;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by tuke on 2021/2/8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepositoryAfterReturning {

    /**
     * support for Spring Expression
     * example: args = {"'complex'", "#list.![id]", "#tenantId"}
     */
    String[] args() default {};

    /**
     * CURD type
     */
    Type type();

    enum Type {
        INSERT,
        UPDATE,
        SELECT,
        DELETE;
    }
}
