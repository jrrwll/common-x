package org.dreamcat.common.spring.exception;

import org.springframework.http.HttpStatus;

/**
 * Create by tuke on 2020/11/2
 */
public class ForbiddenMessageException extends HttpMessageException {

    public ForbiddenMessageException(MessageCode messageCode, Object... args) {
        super(messageCode, args);
    }

    public ForbiddenMessageException(Throwable cause, MessageCode messageCode, Object... args) {
        super(cause, messageCode, args);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
