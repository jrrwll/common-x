package org.dreamcat.common.spring.servlet;

import org.dreamcat.common.spring.result.RestResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Create by tuke on 2021/1/30
 */
@RestControllerAdvice
public class RestResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final String CONTROLLER_SUFFIX = "Controller";

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Class<?> paramType = returnType.getParameterType();
        String controllerName = returnType.getContainingClass().getSimpleName();
        return controllerName.endsWith(CONTROLLER_SUFFIX) &&
                !(RestResult.class.isAssignableFrom(paramType));
    }

    @Override
    public Object beforeBodyWrite(
            Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {
        return RestResult.ok(body);
    }
}
