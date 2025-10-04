package org.dreamcat.common.spring.features.repository;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.dreamcat.common.spring.util.SpelUtil;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2021/2/8
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RepositoryAfterReturningAspect {

    private static final String RETURN_VALUE_PARAMETER_NAME = "result";

    private final RepositoryAfterReturningAdvice repositoryAfterReturningAdvice;

    @AfterReturning(pointcut = "@annotation(repositoryAfterReturning)", returning = "result")
    public void afterReturning(
            JoinPoint joinpoint, Object result,
            RepositoryAfterReturning repositoryAfterReturning) {
        String[] expressions = repositoryAfterReturning.args();
        RepositoryAfterReturning.Type type = repositoryAfterReturning.type();

        int length = expressions.length;
        Object[] args = new Object[length];
        for (int i = 0; i < length; i++) {
            String expression = expressions[i];
            args[i] = SpelUtil.eval(expression, joinpoint,
                    RETURN_VALUE_PARAMETER_NAME, result);
        }

        switch (type) {
            case INSERT:
                repositoryAfterReturningAdvice.insert(args);
                break;
            case UPDATE:
                repositoryAfterReturningAdvice.update(args);
                break;
            case SELECT:
                repositoryAfterReturningAdvice.select(args);
                break;
            case DELETE:
                repositoryAfterReturningAdvice.delete(args);
                break;
            default:
                break;
        }

        if (log.isDebugEnabled()) {
            log.debug("AOP afterReturning @RepositoryAfterReturning: type={}, args={}",
                    type, Arrays.deepToString(args));
        }
    }

}
