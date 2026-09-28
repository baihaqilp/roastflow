package com.flx.porto.roastflow.common.exception;

import java.time.OffsetDateTime;

public record ApiErrorResponse (
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path
){
}
