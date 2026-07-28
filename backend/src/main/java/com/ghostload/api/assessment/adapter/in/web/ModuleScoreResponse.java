package com.ghostload.api.assessment.adapter.in.web;

import com.ghostload.api.assessment.domain.model.BenchmarkModule;
public record ModuleScoreResponse(BenchmarkModule module, double score) {}
