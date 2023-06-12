package org.dreamcat.common.spring.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Create by tuke on 2020/11/2
 */
@Getter
public class HttpMessageException extends RuntimeException {

    private final MessageCode messageCode;
    private final Object[] args;

    public HttpMessageException(MessageCode messageCode, Object... args) {
        this.messageCode = messageCode;
        this.args = args;
    }

    public HttpMessageException(Throwable cause, MessageCode messageCode, Object... args) {
        super(cause);
        this.messageCode = messageCode;
        this.args = args;
    }

    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
