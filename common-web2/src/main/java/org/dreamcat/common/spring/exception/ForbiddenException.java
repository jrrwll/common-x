package org.dreamcat.common.spring.exception;

import org.springframework.http.HttpStatus;

/**
 * Create by tuke on 2020/2/23
 */
public class ForbiddenException extends HttpException {

    public ForbiddenException(int code) {
        super(code);
    }

    public ForbiddenException(int code, String message) {
        super(code, message);
    }

    public ForbiddenException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public ForbiddenException(int code, Throwable cause) {
        super(code, cause);
    }

    public ForbiddenException() {
        this(-1);
    }

    public ForbiddenException(String message) {
        this(-1, message);
    }

    public ForbiddenException(String message, Throwable cause) {
        this(-1, message, cause);
    }

    public ForbiddenException(Throwable cause) {
        this(-1, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
