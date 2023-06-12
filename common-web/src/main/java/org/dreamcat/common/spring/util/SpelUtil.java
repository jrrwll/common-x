package org.dreamcat.common.spring.util;

import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.dreamcat.common.util.ObjectUtil;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Create by tuke on 2018-12-19
 */
@SuppressWarnings({"unchecked"})
public final class SpelUtil {

    private SpelUtil() {
    }

    public static Object eval(String expressionString, Object proxy, Object[] args) {
        EvaluationContext context = getEvaluationContext(proxy, args);
        Expression expression = new SpelExpressionParser().parseExpression(expressionString);
        return expression.getValue(context);
    }

    public static <T> T eval(String expressionString, Object proxy, Object[] args, Class<T> type) {
        EvaluationContext context = getEvaluationContext(proxy, args);
        Expression expression = new SpelExpressionParser().parseExpression(expressionString);
        return expression.getValue(context, type);
    }

    private static EvaluationContext getEvaluationContext(Object proxy, Object[] args) {
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("$0", proxy);
        for (int i = 0; i < args.length; i++) {
            context.setVariable("$" + i, args[i]);
        }
        return context;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static <T> T eval(String expression, JoinPoint joinpoint, Object... variables) {
        Object target = joinpoint.getTarget();
        MethodSignature signature = (MethodSignature) joinpoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinpoint.getArgs();
        EvaluationContext evaluationContext = new MethodBasedEvaluationContext(
                target, method, args, new DefaultParameterNameDiscoverer());
        if (ObjectUtil.isNotEmpty(variables)) {
            int length = variables.length;
            ObjectUtil.requireEven(length, "variables.length");
            for (int i = 0; i < length; i += 2) {
                String variableName = (String) variables[i];
                evaluationContext.setVariable(variableName, variables[i + 1]);
            }
        }
        return eval(expression, evaluationContext);
    }

    public static <T> T eval(String expression, EvaluationContext evaluationContext) {
        return (T) new SpelExpressionParser().parseExpression(expression)
                .getValue(evaluationContext);
    }
}
