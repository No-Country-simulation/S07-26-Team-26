package com.ghostload.api.administration.adapter.in.web;

import java.util.List;

public record OperatorPageResponse(
        List<OperatorSummaryResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
