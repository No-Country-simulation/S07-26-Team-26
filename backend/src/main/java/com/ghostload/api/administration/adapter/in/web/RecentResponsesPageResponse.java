package com.ghostload.api.administration.adapter.in.web;

import java.util.List;

public record RecentResponsesPageResponse(
        List<RecentResponseItemResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}