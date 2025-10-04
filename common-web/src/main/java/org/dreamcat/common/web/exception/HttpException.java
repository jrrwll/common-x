package org.dreamcat.common.web.exception;

import lombok.Getter;
import org.dreamcat.common.web.result.CommonResult;

/**
 * Create by tuke on 2019-04-18
 */
@Getter
public class HttpException extends RuntimeException {

    private final int code;

    public HttpException(int code) {
        super();
        this.code = code;
    }

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public HttpException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public HttpException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public HttpException() {
        this(CommonResult.DEFAULT_ERROR_CODE);
    }

    public HttpException(String message) {
        this(CommonResult.DEFAULT_ERROR_CODE, message);
    }

    public HttpException(String message, Throwable cause) {
        this(CommonResult.DEFAULT_ERROR_CODE, message, cause);
    }

    public HttpException(Throwable cause) {
        this(CommonResult.DEFAULT_ERROR_CODE, cause);
    }

    public int getStatusCode() {
        return 500;
    }
}
