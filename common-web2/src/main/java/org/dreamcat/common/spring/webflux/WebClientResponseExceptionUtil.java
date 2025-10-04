package org.dreamcat.common.spring.webflux;

import java.nio.charset.StandardCharsets;
import org.dreamcat.common.json.JsonUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Create by tuke on 2020/3/20
 */
public final class WebClientResponseExceptionUtil {

    private WebClientResponseExceptionUtil() {
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
