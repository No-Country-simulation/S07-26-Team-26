package com.ghostload.api.assessment.adapter.out.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "benchmark_questions")
public class BenchmarkQuestionJpaEntity {
    @Id private UUID id;
    @Column(nullable = false, length = 20) private String version;
    @Column(name = "module_code", nullable = false, length = 40) private String moduleCode;
    @Column(name = "question_order", nullable = false) private int questionOrder;
    @Column(nullable = false, length = 500) private String text;
    @Column(nullable = false) private boolean active;
    protected BenchmarkQuestionJpaEntity() {}
    public UUID getId() { return id; }
    public String getVersion() { return version; }
    public String getModuleCode() { return moduleCode; }
    public int getQuestionOrder() { return questionOrder; }
    public String getText() { return text; }
    public boolean isActive() { return active; }
}
