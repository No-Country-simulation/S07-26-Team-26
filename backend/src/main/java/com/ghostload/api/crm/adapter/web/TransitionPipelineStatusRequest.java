package com.ghostload.api.crm.adapter.web;

import com.ghostload.api.crm.domain.model.PipelineStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransitionPipelineStatusRequest(
        @NotNull PipelineStatus status,
        @Size(max = 2_000) String note) {
}