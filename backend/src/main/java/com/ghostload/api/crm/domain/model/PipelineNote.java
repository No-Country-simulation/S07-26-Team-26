package com.ghostload.api.crm.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PipelineNote(
        UUID id,
        String note,
        Instant createdAt) {
}