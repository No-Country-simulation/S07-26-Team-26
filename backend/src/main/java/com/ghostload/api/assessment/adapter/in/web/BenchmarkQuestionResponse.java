package com.ghostload.api.assessment.adapter.in.web;

import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import java.util.List;
import java.util.UUID;

public record BenchmarkQuestionResponse(UUID id, String version, BenchmarkModule module, int order, String text,
                                        boolean active, List<BenchmarkScaleOptionResponse> scale) {}
