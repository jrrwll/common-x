package org.dreamcat.common.spring.features.accesslimit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by tuke on 2021/1/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {

    /**
     * access count
     */
    int count();

    /**
     * in seconds
     */
    long timespan();

    /**
     * Spring EL is supported
     */
    String key() default "";
}
