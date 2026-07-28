package com.ghostload.api.shared.adapter.in.web;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId,
        List<FieldErrorResponse> fieldErrors) {
}
