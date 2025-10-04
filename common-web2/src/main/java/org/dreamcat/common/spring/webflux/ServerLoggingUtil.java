package org.dreamcat.common.spring.webflux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.json.JsonUtil;
import org.dreamcat.common.util.ObjectUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

/**
 * Create by tuke on 2020/3/22
 */
@Slf4j
public final class ServerLoggingUtil {

    private ServerLoggingUtil() {
    }

    public static void log(ServerWebExchange exchange, ResponseStatusException e) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        List<String> messages = new ArrayList<>(reqLines(request));
        messages.add("<");

        Map<String, String> headers = toMap(response.getHeaders());
        HttpStatus status = response.getStatusCode();
        if (e != null) {
            status = e.getStatus();
            Map<String, String> responseHeaders = toMap(e.getResponseHeaders());
            responseHeaders.keySet().forEach(it -> {
                if (!headers.containsKey(it)) {
                    headers.put(it, responseHeaders.get(it));
                }
            });
        }
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        messages.add(String.format("< HTTP/1.1 %s: %s", status.value(),
                status.getReasonPhrase()));
        fillHeaders(messages, headers, "<");
        if (e != null && e.getReason() != null) {
            messages.add(String.format("* %s", e.getReason()));
        }
        log.info("\n{}", String.join("\n", messages));
    }

    public static void log(ServerWebExchange exchange) {
        log(exchange, null, null);
    }

    public static void log(ServerWebExchange exchange, Object requestBody, Object responseBody) {
        log(exchange, requestBody, responseBody, null);
    }

    public static void log(ServerWebExchange exchange, Object requestBody, Object responseBody,
            Long cost) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        List<String> messages = new ArrayList<>(reqLines(request));
        if (requestBody != null) {
            String req = requestBody instanceof String ? (String) requestBody :
                    JsonUtil.toJson(requestBody);
            messages.add(String.format("* %s", req));
        }
        messages.add("<");
        messages.addAll(resLines(response));
        if (responseBody != null) {
            String res = responseBody instanceof String ? (String) responseBody :
                    JsonUtil.toJson(responseBody);
            messages.add(String.format("* %s", res));
        }
        if (cost != null) {
            messages.add(String.format("* cost %d ms", cost));
        }
        log.info("\n{}", String.join("\n", messages));
    }

    private static List<String> reqLines(ServerHttpRequest request) {
        String method = request.getMethodValue();
        String path = request.getPath().value();

        List<String> messages = new ArrayList<>();
        messages.add(String.format("> %s %s HTTP/1.1",
                method.toUpperCase(), path));
        fillHeaders(messages, toMap(request.getHeaders()), ">");
        return messages;
    }

    private static List<String> resLines(ServerHttpResponse response) {
        HttpStatus status = response.getStatusCode();
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        List<String> messages = new ArrayList<>();
        messages.add(String.format("< HTTP/1.1 %s: %s", status.value(),
                status.getReasonPhrase()));
        fillHeaders(messages, toMap(response.getHeaders()), "<");
        return messages;
    }

    private static Map<String, String> toMap(HttpHeaders headers) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            if (ObjectUtil.isEmpty(values)) continue;
            map.put(name, String.join(", ", values));
        }
        return map;
    }

    private static void fillHeaders(List<String> messages, Map<String, String> headers,
            String prefix) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            messages.add(String.format("%s %s: %s", prefix, name, value));
        }
    }
}
