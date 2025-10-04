package org.dreamcat.common.spring.exception;

import org.springframework.http.HttpStatus;

/**
 * Create by tuke on 2020/3/21
 */
public class InternalServerErrorException extends HttpException {

    public InternalServerErrorException(int code) {
        super(code);
    }

    public InternalServerErrorException(int code, String message) {
        super(code, message);
    }

    public InternalServerErrorException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public InternalServerErrorException(int code, Throwable cause) {
        super(code, cause);
    }

    public InternalServerErrorException() {
        this(-1);
    }

    public InternalServerErrorException(String message) {
        this(-1, message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        this(-1, message, cause);
    }

    public InternalServerErrorException(Throwable cause) {
        this(-1, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
