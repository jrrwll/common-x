package org.dreamcat.common.spring.features.accesslimit;

/**
 * Create by tuke on 2021/3/10
 */
public interface AccessLimitAdvice {

    int increment(String key, long timespan);

    void trigger(String key);
}
