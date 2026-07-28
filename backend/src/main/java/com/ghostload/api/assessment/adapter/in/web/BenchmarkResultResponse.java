package com.ghostload.api.assessment.adapter.in.web;

import com.ghostload.api.assessment.domain.model.MaturityLevel;
import java.time.Instant;
import java.util.List;
public record BenchmarkResultResponse(double totalScore, MaturityLevel maturityLevel, double percentile,
                                      String percentileDisclaimer, List<ModuleScoreResponse> moduleScores, Instant completedAt) {}
