package com.ghostload.api.crm.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PipelineStatusChange(
        UUID id,
        PipelineStatus fromStatus,
        PipelineStatus toStatus,
        Instant changedAt) {
}