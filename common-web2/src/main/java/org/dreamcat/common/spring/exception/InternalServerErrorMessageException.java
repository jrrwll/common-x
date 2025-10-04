package org.dreamcat.common.spring.exception;

import org.springframework.http.HttpStatus;

/**
 * Create by tuke on 2020/11/2
 */
public class InternalServerErrorMessageException extends HttpMessageException {

    public InternalServerErrorMessageException(MessageCode messageCode, Object... args) {
        super(messageCode, args);
    }

    public InternalServerErrorMessageException(Throwable cause, MessageCode messageCode,
            Object... args) {
        super(cause, messageCode, args);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
