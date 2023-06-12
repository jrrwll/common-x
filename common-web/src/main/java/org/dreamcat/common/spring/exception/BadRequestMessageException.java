package org.dreamcat.common.spring.exception;

import org.springframework.http.HttpStatus;

/**
 * Create by tuke on 2020/11/2
 */
public class BadRequestMessageException extends HttpMessageException {

    public BadRequestMessageException(MessageCode messageCode, Object... args) {
        super(messageCode, args);
    }

    public BadRequestMessageException(Throwable cause, MessageCode messageCode, Object... args) {
        super(cause, messageCode, args);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
