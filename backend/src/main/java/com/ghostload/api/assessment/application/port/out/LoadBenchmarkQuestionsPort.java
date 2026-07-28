package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.BenchmarkQuestion;
import java.util.List;

public interface LoadBenchmarkQuestionsPort {
    List<BenchmarkQuestion> findActiveByVersion(String version);
}
