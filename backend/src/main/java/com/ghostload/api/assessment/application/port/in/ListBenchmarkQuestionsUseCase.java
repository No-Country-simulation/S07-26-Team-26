package com.ghostload.api.assessment.application.port.in;

import com.ghostload.api.assessment.domain.model.BenchmarkQuestion;
import java.util.List;

public interface ListBenchmarkQuestionsUseCase {
    List<BenchmarkQuestion> list(String version);
}
