package org.dreamcat.common.spring.webflux;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

/**
 * Create by tuke on 2020/3/22
 */
public class LoggingServerWebExchangeDecorator extends ServerWebExchangeDecorator {

    private final LoggingServerHttpRequestDecorator request;
    private final LoggingServerHttpResponseDecorator response;

    protected LoggingServerWebExchangeDecorator(ServerWebExchange delegate, boolean saveBody) {
        super(delegate);
        this.request = new LoggingServerHttpRequestDecorator(super.getRequest(), saveBody);
        this.response = new LoggingServerHttpResponseDecorator(super.getResponse(), saveBody);
    }

    @Override
    public ServerHttpRequest getRequest() {
        return request;
    }

    @Override
    public ServerHttpResponse getResponse() {
        return response;
    }

    public void log() {
        long cost = response.getEndTime() - request.getStartTime();
        String requestBody = request.getRequestBody();
        String responseBody = response.getResponseBody();
        ServerLoggingUtil.log(getDelegate(), requestBody, responseBody, cost);
    }
}
