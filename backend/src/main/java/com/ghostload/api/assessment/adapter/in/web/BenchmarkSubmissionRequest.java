package com.ghostload.api.assessment.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BenchmarkSubmissionRequest(@NotBlank String questionnaireVersion,
                                         @NotNull @Size(min = 20, max = 20) List<@Valid BenchmarkAnswerRequest> answers) {}
