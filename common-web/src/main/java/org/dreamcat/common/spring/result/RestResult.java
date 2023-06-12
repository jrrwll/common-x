package org.dreamcat.common.spring.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Create by tuke on 2019-02-02
 * <p>
 * When I try to compliance with common rules of restful arch,
 * I find out it's difficult to unite the response structures of vendors.
 * So I believe in the saying: <h3>less is more</h3>,
 * and keep it as my custom response structure.
 */
@Data
//@JsonSerialize(using = RestBody.Serializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResult<T> {

    public static final int DEFAULT_OK_CODE = 0;
    public static final int DEFAULT_ERROR_CODE = -1;
    public static final RestResult<?> OK = ok();
    public static final RestResult<?> ERROR = error("error");

    // maybe not equal 0 when http code is 2xx
    private final int code;
    // always equals null when code is 0
    private final String message;
    // wrapped by data
    private final T data;

    public RestResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> RestResult<T> ok() {
        return ok(null);
    }

    public static <T> RestResult<T> ok(T data) {
        return create(DEFAULT_OK_CODE, null, data);
    }

    public static <T> RestResult<T> error(String message) {
        return create(DEFAULT_ERROR_CODE, message);
    }

    public static <T> RestResult<T> error(int code, String message) {
        return create(code, message);
    }

    public static <T> RestResult<T> error(int code, String msg, T data) {
        if (code == DEFAULT_OK_CODE) {
            throw new IllegalArgumentException(
                    "code must not be " + DEFAULT_OK_CODE + " in error case");
        }
        return create(code, msg, data);
    }

    public static <T, R> RestResult<R> error(RestResult<T> body) {
        return error(body.getCode(), body.getMessage());
    }

    public static <T> RestResult<T> create(boolean success, String message) {
        return create(success ? DEFAULT_OK_CODE : DEFAULT_ERROR_CODE, message);
    }

    public static <T> RestResult<T> create(boolean success, String format, Object... args) {
        return create(success, String.format(format, args));
    }

    public static <T> RestResult<T> create(int code) {
        return create(code, null);
    }

    public static <T> RestResult<T> create(int code, String msg) {
        return create(code, msg, null);
    }

    /**
     * create restful response body
     *
     * @param code code field
     * @param msg  maybe not null when code is 0
     * @param data maybe not null when code is not 0
     * @param <T>  data type
     * @return restful response body
     */
    public static <T> RestResult<T> create(int code, String msg, T data) {
        return new RestResult<>(code, msg, data);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return code == DEFAULT_OK_CODE;
    }

    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

}
