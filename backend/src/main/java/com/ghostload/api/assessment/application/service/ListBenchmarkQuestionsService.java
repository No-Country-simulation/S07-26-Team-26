package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.ListBenchmarkQuestionsUseCase;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkQuestionsPort;
import com.ghostload.api.assessment.domain.model.BenchmarkQuestion;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListBenchmarkQuestionsService implements ListBenchmarkQuestionsUseCase {
    private final LoadBenchmarkQuestionsPort questionsPort;
    public ListBenchmarkQuestionsService(LoadBenchmarkQuestionsPort questionsPort) { this.questionsPort = questionsPort; }
    @Override public List<BenchmarkQuestion> list(String version) {
        List<BenchmarkQuestion> questions = questionsPort.findActiveByVersion(version == null || version.isBlank() ? "v1" : version);
        if (questions.isEmpty()) throw new IllegalArgumentException("No existe una versión activa del benchmark para " + version + ".");
        return questions;
    }
}
