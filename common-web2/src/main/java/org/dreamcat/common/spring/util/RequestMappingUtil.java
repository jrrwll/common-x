package org.dreamcat.common.spring.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import org.dreamcat.common.io.UrlUtil;
import org.dreamcat.common.util.FunctionUtil;
import org.dreamcat.common.util.ReflectUtil;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

/**
 * @author Jerry Will
 * @version 2021-11-09
 */
public class RequestMappingUtil {

    private RequestMappingUtil() {
    }

    public static Object invokeGetMapping(
            Object controller, String methodName, String query, Map<String, Object> extraParameters)
            throws NoSuchMethodException {
        Class<?> clazz = controller.getClass();
        Method method = Arrays.stream(clazz.getDeclaredMethods())
                .filter(it -> it.getName().equals(methodName))
                .findAny().orElseThrow(() -> new NoSuchMethodException(clazz.getName() + "." + methodName));
        return invokeGetMapping(controller, method, query, extraParameters);
    }

    public static Object invokeGetMapping(
            Object controller, Method method, String query, Map<String, Object> extraParameters) {
        Map<String, String> queryMap = UrlUtil.toQueryMap(query);

        Parameter[] parameters = method.getParameters();
        int length = parameters.length;
        Object[] args = new Object[length];
        for (int i = 0; i < length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            String parameterName = parameter.getName();

            Object extraParameter = extraParameters.get(parameterName);
            if (extraParameter != null) {
                args[i] = extraParameter;
                continue;
            }

            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                parameterName = FunctionUtil.firstNotBlank(requestParam.name(), requestParam.value(), parameterName);
                String defaultValue = requestParam.defaultValue();
                if (!defaultValue.equals(ValueConstants.DEFAULT_NONE)) {
                    queryMap.putIfAbsent(parameterName, defaultValue);
                }
            }
            String parameterValue = queryMap.get(parameterName);
            args[i] = ReflectUtil.parse(parameterValue, parameterType);
        }
        try {
            return method.invoke(controller, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
