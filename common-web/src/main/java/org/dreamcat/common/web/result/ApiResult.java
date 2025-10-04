package org.dreamcat.common.web.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jerry Will
 * @version 2025-10-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    public static final String DEFAULT_OK_CODE = "ok";
    public static final String DEFAULT_ERROR_CODE = "err";
    public static final String DEFAULT_ERROR_ARG_NAME = "msg";
    
    public static final ApiResult<?> OK = ok();

    private String errCode;
    private Map<String, Object> errArgs;
    private T data;

    public static <T> ApiResult<T> ok() {
        return ok(null);
    }

    public static <T> ApiResult<T> ok(T data) {
        return create(DEFAULT_OK_CODE, null, data);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static <T, R> ApiResult<R> error(ApiResult<T> body) {
        return error(body.getErrCode(), body.getErrArgs());
    }

    public static <T> ApiResult<T> error(String message) {
        return error(DEFAULT_ERROR_CODE, message);
    }

    public static <T> ApiResult<T> error(String code, String message) {
        return error(code, Collections.singletonMap(DEFAULT_ERROR_ARG_NAME, message));
    }

    public static <T> ApiResult<T> error(String code, Map<String, Object> errArgs) {
        if (Objects.equals(code, DEFAULT_OK_CODE)) {
            throw new IllegalArgumentException(
                    "code must not be " + DEFAULT_OK_CODE + " in error case");
        }
        return create(code, errArgs, null);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    /**
     * create restful response body
     *
     * @param errCode code field
     * @param errArgs  must not null when code is not ok
     * @param data must not null when code is ok
     * @param <T>  data type
     * @return restful response body
     */
    private static <T> ApiResult<T> create(String errCode, Map<String, Object> errArgs, T data) {
        return new ApiResult<>(errCode, errArgs, data);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return Objects.equals(errCode, DEFAULT_OK_CODE);
    }

    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

}
