package com.ghostload.api.assessment.adapter.in.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BenchmarkAnswerRequest(@NotNull UUID questionId, @Min(1) @Max(5) int value) {}
