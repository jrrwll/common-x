package org.dreamcat.common.spring.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Create by tuke on 2020/3/22
 */
@Slf4j
public class LoggingWebFilter implements WebFilter {

    private final Level level;

    public LoggingWebFilter() {
        this(Level.HEADER);
    }

    public LoggingWebFilter(Level level) {
        this.level = level;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        boolean saveBody = Level.BODY.equals(level);
        LoggingServerWebExchangeDecorator exchangeDecorator = new LoggingServerWebExchangeDecorator(
                exchange, saveBody);
        return chain.filter(exchangeDecorator)
                .doOnSuccess(aVoid -> exchangeDecorator.log())
                .doOnError(e -> {
                    if (e instanceof ResponseStatusException) {
                        String reason = ((ResponseStatusException) e).getReason();
                        if (reason != null) {
                            log.error(reason);
                            return;
                        }
                    }
                    log.error(e.getMessage());
                });
    }

    public enum Level {
        HEADER,
        BODY;
    }
}
