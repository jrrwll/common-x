package org.dreamcat.common.web.spring.webflux;

import org.dreamcat.common.json.JsonUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Create by tuke on 2020/3/20
 */
public final class FluxUtil {

    private FluxUtil() {
    }

    public static <T> Mono<ServerResponse> badRequest(T value) {
        return ServerResponse.badRequest()
                .body(BodyInserters.fromValue(value));
    }

    public static WebClientResponseException create(int statusCode) {
        return create(statusCode, null);
    }

    public static WebClientResponseException create(int statusCode, Object body) {
        String reasonPhrase = HttpStatus.valueOf(statusCode).getReasonPhrase();
        byte[] bodyBytes = body == null ? new byte[0]
                : JsonUtil.toJson(body).getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new WebClientResponseException(
                statusCode, reasonPhrase, headers, bodyBytes, StandardCharsets.UTF_8);
    }
}
