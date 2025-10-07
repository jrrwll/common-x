package org.dreamcat.common.web.exception;

import lombok.Getter;
import org.dreamcat.common.web.result.ApiResult;

/**
 * Create by tuke on 2019-04-18
 */
@Getter
public class ApiException extends RuntimeException {

    private final int code; // error code, not http status code

    public ApiException(int code) {
        super();
        this.code = code;
    }

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ApiException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public ApiException() {
        this(ApiResult.DEFAULT_ERROR_CODE);
    }

    public ApiException(String message) {
        this(ApiResult.DEFAULT_ERROR_CODE, message);
    }

    public ApiException(String message, Throwable cause) {
        this(ApiResult.DEFAULT_ERROR_CODE, message, cause);
    }

    public ApiException(Throwable cause) {
        this(ApiResult.DEFAULT_ERROR_CODE, cause);
    }

    public int getStatusCode() {
        return 200;
    }
}
