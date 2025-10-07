package org.dreamcat.common.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.web.result.ApiResult;
import org.dreamcat.rita.exception.ExceptionHandler;
import org.dreamcat.rita.http.Request;
import org.dreamcat.rita.renderer.ResponseEntity;
/**
 * @author Jerry Will
 * @version 2025-10-05
 */
@Slf4j
public class RitaApiExceptionHandler implements ExceptionHandler {

    @Override
    public Object handle(Request req, Exception e) {
        if (!(e instanceof ApiException)) {
            return ApiResult.error(e.getMessage());
        }

        ApiException ex = (ApiException) e;
        return ResponseEntity.fromStatusCode(ex.getStatusCode())
                .body(ApiResult.error(ex.getCode(), e.getMessage()));
    }
}
