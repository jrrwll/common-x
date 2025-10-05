package org.dreamcat.common.web.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Create by tuke on 2019-02-02
 * <p>
 * When I try to compliance with common rules of restful arch,
 * I find out it's difficult to unite the response structures of vendors.
 * So I believe in the saying: <h3>less is more</h3>,
 * and keep it as my custom response structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonSerialize(using = RestBody.Serializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    public static final int DEFAULT_OK_CODE = 0;
    public static final int DEFAULT_ERROR_CODE = 1;

    public static final ApiResult<?> OK = ok();

    private int code;
    private String msg;
    private T data;

    public static <T> ApiResult<T> ok() {
        return ok(null);
    }

    public static <T> ApiResult<T> ok(T data) {
        return create(DEFAULT_OK_CODE, null, data);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static <T, R> ApiResult<R> error(ApiResult<T> body) {
        return error(body.getCode(), body.getMsg());
    }

    public static <T> ApiResult<T> error(String message) {
        return error(DEFAULT_ERROR_CODE, message);
    }

    public static <T> ApiResult<T> error(int code, String message) {
        return create(code, message, null);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    /**
     * create restful response body
     *
     * @param code code field
     * @param msg must not null when code is not ok
     * @param data    must not null when code is ok
     * @param <T>     data type
     * @return restful response body
     */
    private static <T> ApiResult<T> create(int code, String msg, T data) {
        return new ApiResult<>(code, msg, data);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return Objects.equals(code, DEFAULT_OK_CODE);
    }

    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

}
