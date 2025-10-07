package org.dreamcat.common.web.exception;

/**
 * Create by tuke on 2019-04-18
 */
public class NotFoundException extends ApiException {

    @Override
    public int getStatusCode() {
        return 404;
    }

    public NotFoundException(int code) {
        super(code);
    }

    public NotFoundException(int code, String message) {
        super(code, message);
    }

    public NotFoundException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public NotFoundException(int code, Throwable cause) {
        super(code, cause);
    }

    public NotFoundException() {
        this(-1);
    }

    public NotFoundException(String message) {
        this(-1, message);
    }

    public NotFoundException(String message, Throwable cause) {
        this(-1, message, cause);
    }

    public NotFoundException(Throwable cause) {
        this(-1, cause);
    }
}
