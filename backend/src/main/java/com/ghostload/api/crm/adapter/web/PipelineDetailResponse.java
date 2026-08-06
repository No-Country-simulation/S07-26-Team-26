package com.ghostload.api.crm.adapter.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PipelineDetailResponse(
        UUID id,
        String companyName,
        String contactName,
        String email,
        String region,
        Double benchmarkScore,
        String status,
        Instant createdAt,
        Instant updatedAt,
        List<PipelineNoteResponse> notes,
        List<PipelineHistoryResponse> history) {

    public PipelineDetailResponse {
        notes = List.copyOf(notes);
        history = List.copyOf(history);
    }

    public record PipelineNoteResponse(UUID id, String note, Instant createdAt) {
    }

    public record PipelineHistoryResponse(
            UUID id,
            String fromStatus,
            String toStatus,
            Instant changedAt) {
    }
}