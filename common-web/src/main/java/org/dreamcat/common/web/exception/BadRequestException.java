package org.dreamcat.common.web.exception;

import org.springframework.http.HttpStatus;

/**
 * Create by tuke on 2019-02-02
 */
public class BadRequestException extends HttpException {

    @Override
    public int getStatusCode() {
        return 400;
    }

    public BadRequestException(int code) {
        super(code);
    }

    public BadRequestException(int code, String message) {
        super(code, message);
    }

    public BadRequestException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public BadRequestException(int code, Throwable cause) {
        super(code, cause);
    }

    public BadRequestException() {
        this(-1);
    }

    public BadRequestException(String message) {
        this(-1, message);
    }

    public BadRequestException(String message, Throwable cause) {
        this(-1, message, cause);
    }

    public BadRequestException(Throwable cause) {
        this(-1, cause);
    }
}
