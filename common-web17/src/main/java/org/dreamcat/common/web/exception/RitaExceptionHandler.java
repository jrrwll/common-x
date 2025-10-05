package org.dreamcat.common.web.exception;

import org.dreamcat.common.web.result.ApiResult;
import org.dreamcat.rita.annotation.Provider;
import org.dreamcat.rita.exception.ExceptionHandler;
import org.dreamcat.rita.http.Request;
import org.dreamcat.rita.renderer.ResponseEntity;

/**
 * @author Jerry Will
 * @version 2025-10-05
 */
@Provider
public class RitaExceptionHandler implements ExceptionHandler {

    @Override
    public Object handle(Request req, Exception e) {
        if (e instanceof HttpException) {

        }
        return ApiResult.error(e.getMessage());
    }
}
