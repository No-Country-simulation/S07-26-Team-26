package com.ghostload.api.crm.adapter.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePipelineEntryRequest(
        @NotBlank @Size(max = 160) String companyName,
        @Size(max = 160) String contactName,
        @Size(max = 254) String email,
        @Size(max = 120) String region,
        @Min(0) @Max(100) Double benchmarkScore) {
}