package com.ghostload.api.crm.adapter.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddPipelineNoteRequest(
        @NotBlank @Size(max = 2_000) String note) {
}