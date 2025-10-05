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
 * for i18n of error message, require an external error-message service
 *
 * @author Jerry Will
 * @version 2025-10-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiDetailResult<T> {

    public static final String DEFAULT_OK_CODE = "ok";
    public static final String DEFAULT_ERROR_CODE = "err";
    public static final String DEFAULT_ERROR_ARG_NAME = "msg";

    public static final ApiDetailResult<?> OK = ok();

    private String errCode;
    private Map<String, Object> errArgs;
    private T data;

    public static <T> ApiDetailResult<T> ok() {
        return ok(null);
    }

    public static <T> ApiDetailResult<T> ok(T data) {
        return create(DEFAULT_OK_CODE, null, data);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static <T, R> ApiDetailResult<R> error(ApiDetailResult<T> body) {
        return error(body.getErrCode(), body.getErrArgs());
    }

    public static <T> ApiDetailResult<T> error(String message) {
        return error(DEFAULT_ERROR_CODE, message);
    }

    public static <T> ApiDetailResult<T> error(String code, String message) {
        return error(code, Collections.singletonMap(DEFAULT_ERROR_ARG_NAME, message));
    }

    public static <T> ApiDetailResult<T> error(String code, Map<String, Object> errArgs) {
        if (Objects.equals(code, DEFAULT_OK_CODE)) {
            throw new IllegalArgumentException(
                    "code must not be " + DEFAULT_OK_CODE + " in error case");
        }
        return create(code, errArgs, null);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    private static <T> ApiDetailResult<T> create(String errCode, Map<String, Object> errArgs, T data) {
        return new ApiDetailResult<>(errCode, errArgs, data);
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
