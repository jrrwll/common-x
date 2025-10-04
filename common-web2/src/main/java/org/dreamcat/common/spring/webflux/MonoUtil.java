package org.dreamcat.common.spring.webflux;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Create by tuke on 2020/3/29
 */
public final class MonoUtil {

    private MonoUtil() {
    }

    public static <T> Mono<ServerResponse> badRequest(T value) {
        return ServerResponse.badRequest()
                .body(BodyInserters.fromValue(value));
    }
}
