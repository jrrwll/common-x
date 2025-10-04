package org.dreamcat.common.spring.exception;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.spring.i18n.MessageSourceComponent;
import org.dreamcat.common.spring.result.RestResult;
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
public class RestExceptionHandler {

    private final MessageSourceComponent messageSource;

    /**
     * thrown manually
     *
     * @param e include {@link HttpStatus}, the custom error code and i18n messages
     * @return response wrapped with {@link RestResult}
     */
    @ExceptionHandler(HttpException.class)
    @Order(HIGHEST_PRECEDENCE)
    public ResponseEntity<RestResult<?>> handleHttpException(HttpException e) {
        int code = e.getCode();
        String message = e.getMessage();
        HttpStatus status = e.getStatus();
        return ResponseEntity.status(status).body(RestResult.error(code, message));
    }

    /**
     * thrown manually
     *
     * @param e include {@link HttpStatus} and the custom error code
     * @return response wrapped with {@link RestResult}
     */
    @ExceptionHandler(HttpMessageException.class)
    @Order(HIGHEST_PRECEDENCE)
    public ResponseEntity<RestResult<?>> handleHttpMessageException(HttpMessageException e,
            Locale locale) {
        MessageCode messageCode = e.getMessageCode();
        Object[] args = e.getArgs();
        HttpStatus status = e.getStatus();
        RestResult<?> body = messageSource.getMessage(messageCode, args, locale);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * thrown when binding errors are considered fatal
     *
     * @param e implements the {@link BindingResult} interface
     * @return response wrapped with {@link RestResult}
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<?> handleBindException(BindException e) {
        String message = e.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RestResult.error(message));
    }

    /**
     * thrown when binding errors are considered fatal
     *
     * @param e thrown when validation on an argument annotated with {@code @Valid} fails
     * @return response wrapped with {@link RestResult}
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String message = e.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RestResult.error(message));
    }
}
