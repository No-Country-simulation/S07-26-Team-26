package com.ghostload.api.crm.adapter.web;

import java.time.Instant;
import java.util.UUID;

public record PipelineEntryResponse(
        UUID id,
        String companyName,
        String contactName,
        String email,
        String region,
        Double benchmarkScore,
        String status,
        int noteCount,
        Instant createdAt,
        Instant updatedAt) {
}