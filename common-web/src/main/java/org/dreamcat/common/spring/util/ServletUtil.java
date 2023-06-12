package org.dreamcat.common.spring.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.json.JsonUtil;
import org.dreamcat.common.spring.servlet.HeaderLoggingInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Create by tuke on 2019-05-16
 */
@Slf4j
public final class ServletUtil {

    private ServletUtil() {
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        return attributes.getRequest();
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T getAttribute(String name) {
        HttpServletRequest request = getRequest();
        if (request == null) return null;
        return (T) request.getAttribute(name);
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        return attributes.getResponse();
    }

    public static void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    /**
     * @see HeaderLoggingInterceptor
     */
    public static void log(HttpServletRequest request, HttpServletResponse response) {
        // request
        List<String> messages = new ArrayList<>(reqLines(request));
        messages.add(">");
        // response
        messages.addAll(resLines(response));
        messages.add("<");
        log.info("\n{}", String.join("\n", messages));
    }

    public static void log() {
        // request
        List<String> messages = new ArrayList<>(reqLines());
        messages.add(">");
        // response
        messages.addAll(resLines());
        messages.add("<");
        log.info("\n{}", String.join("\n", messages));
    }

    public static void log(HttpServletRequest request, HttpServletResponse response,
            Object[] requestArgs, Object responseBody) {
        // request
        List<String> messages = new ArrayList<>(reqLines(request));
        messages.add(String.format("> (%s)", JsonUtil.toJson(requestArgs)));
        messages.add(">");
        // response
        messages.addAll(resLines(response));
        messages.add(String.format("< (%s)", JsonUtil.toJson(responseBody)));
        messages.add("<");
        log.info("\n{}", String.join("\n", messages));
    }

    public static void log(Object[] requestArgs, Object responseBody) {
        // request
        List<String> messages = new ArrayList<>(reqLines());
        messages.add(String.format("> %s", JsonUtil.toJson(requestArgs)));
        messages.add(">");
        // response
        messages.addAll(resLines());
        messages.add(String.format("< %s", JsonUtil.toJson(responseBody)));
        messages.add("<");
        log.info("\n{}", String.join("\n", messages));
    }

    private static List<String> reqLines() {
        HttpServletRequest request = ServletUtil.getRequest();
        if (request == null) return Collections.emptyList();
        return reqLines(request);
    }

    private static List<String> reqLines(HttpServletRequest request) {
        List<String> messages = new ArrayList<>();
        StringBuffer url = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            url.append('?').append(queryString);
        }
        messages.add(String.format("> %s %s HTTP/1.1",
                request.getMethod().toUpperCase(), url.toString()));
        Enumeration<String> requestHeaderNames = request.getHeaderNames();
        while (requestHeaderNames.hasMoreElements()) {
            String headerName = requestHeaderNames.nextElement();
            messages.add(String.format("> %s: %s", headerName, request.getHeader(headerName)));
        }
        return messages;
    }

    private static List<String> resLines() {
        HttpServletResponse response = ServletUtil.getResponse();
        if (response == null) return Collections.emptyList();
        return resLines(response);
    }

    private static List<String> resLines(HttpServletResponse response) {
        List<String> messages = new ArrayList<>();
        int statusCode = response.getStatus();
        messages.add(String.format("< HTTP/1.1 %s: %s", statusCode,
                HttpStatus.valueOf(statusCode).getReasonPhrase()));
        Collection<String> responseHeaderNames = response.getHeaderNames();
        for (String headerName : responseHeaderNames) {
            messages.add(String.format("< %s: %s", headerName, response.getHeader(headerName)));
        }
        return messages;
    }
}
