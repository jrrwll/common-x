package org.dreamcat.common.web.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.web.result.ApiResult;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Create by tuke on 2019-02-02
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class SpringExceptionHandler {

    /**
     * thrown manually
     *
     * @param e include {@link HttpStatus}, the custom error code and i18n messages
     * @return response wrapped with {@link ApiResult}
     */
    @ExceptionHandler(HttpException.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<ApiResult<?>> handleHttpException(HttpException e) {
        int code = e.getCode();
        String message = e.getMessage();
        int status = e.getStatusCode();
        return ResponseEntity.status(status).body(ApiResult.error(code, message));
    }


    /**
     * thrown when binding errors are considered fatal
     *
     * @param e implements the {@link BindingResult} interface
     * @return response wrapped with {@link ApiResult}
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<?> handleBindException(BindException e) {
        String message = e.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.error(message));
    }

    /**
     * thrown when binding errors are considered fatal
     *
     * @param e thrown when validation on an argument annotated with {@code @Valid} fails
     * @return response wrapped with {@link ApiResult}
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String message = e.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.error(message));
    }
}
