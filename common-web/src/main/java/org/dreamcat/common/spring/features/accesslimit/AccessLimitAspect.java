package org.dreamcat.common.spring.features.accesslimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.dreamcat.common.spring.util.SpelUtil;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2021/1/11
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AccessLimitAspect {

    private final AccessLimitAdvice accessLimitAdvice;

    @Before("@annotation(accessLimit)")
    public void before(JoinPoint joinpoint, AccessLimit accessLimit) {
        int count = accessLimit.count();
        long timespan = accessLimit.timespan();
        String expression = accessLimit.key();
        String key = SpelUtil.eval(expression, joinpoint);

        int incremented = accessLimitAdvice.increment(key, timespan);
        if (incremented >= count) {
            accessLimitAdvice.trigger(key);
        }
    }

}
