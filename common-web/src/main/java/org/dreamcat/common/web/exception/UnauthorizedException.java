package org.dreamcat.common.web.exception;

/**
 * Create by tuke on 2019-02-02
 */
public class UnauthorizedException extends HttpException {

    @Override
    public int getStatusCode() {
        return 401;
    }

    public UnauthorizedException(int code) {
        super(code);
    }

    public UnauthorizedException(int code, String message) {
        super(code, message);
    }

    public UnauthorizedException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public UnauthorizedException(int code, Throwable cause) {
        super(code, cause);
    }

    public UnauthorizedException() {
        this(-1);
    }

    public UnauthorizedException(String message) {
        this(-1, message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        this(-1, message, cause);
    }

    public UnauthorizedException(Throwable cause) {
        this(-1, cause);
    }
}
