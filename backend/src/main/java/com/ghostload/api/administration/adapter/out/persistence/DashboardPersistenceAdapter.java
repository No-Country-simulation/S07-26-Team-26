package com.ghostload.api.administration.adapter.out.persistence;

import com.ghostload.api.administration.application.port.in.DashboardSummary;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryCommand;
import com.ghostload.api.administration.application.port.out.LoadDashboardMetricsPort;
import com.ghostload.api.assessment.adapter.out.persistence.BenchmarkModuleScoreJpaRepository;
import com.ghostload.api.assessment.adapter.out.persistence.BenchmarkResultJpaRepository;
import com.ghostload.api.assessment.adapter.out.persistence.CalculatorResultJpaRepository;
import com.ghostload.api.assessment.adapter.out.persistence.EvaluationJpaRepository;
import com.ghostload.api.assessment.adapter.out.persistence.OperatorJpaRepository;
import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import com.ghostload.api.outreach.adapter.out.persistence.SpringDataContactRepository;
import com.ghostload.api.outreach.adapter.out.persistence.SpringDataInvitationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JPA adapter that implements {@link LoadDashboardMetricsPort}.
 * Queries the relevant tables to aggregate the 4 core admin KPIs plus extended metrics.
 *
 * <p>Core KPIs (Requirement 2, AC-1):
 * <ul>
 *   <li>totalOperators — count of rows in the operators table</li>
 *   <li>benchmarksCompleted — count of benchmark_results within the optional date range</li>
 *   <li>averageScore — average total_score from benchmark_results within the optional date range</li>
 *   <li>pdfsGenerated — equals benchmarksCompleted (each completed evaluation produces a PDF report)</li>
 * </ul>
 *
 * <p>Null from/to values in the command are treated as "no date filter" (all-time).
 */
@Component
public class DashboardPersistenceAdapter implements LoadDashboardMetricsPort {

    private final OperatorJpaRepository operatorJpaRepository;
    private final EvaluationJpaRepository evaluationJpaRepository;
    private final BenchmarkResultJpaRepository benchmarkResultJpaRepository;
    private final BenchmarkModuleScoreJpaRepository benchmarkModuleScoreJpaRepository;
    private final CalculatorResultJpaRepository calculatorResultJpaRepository;
    private final SpringDataContactRepository contactRepository;
    private final SpringDataInvitationRepository invitationRepository;

    public DashboardPersistenceAdapter(
            OperatorJpaRepository operatorJpaRepository,
            EvaluationJpaRepository evaluationJpaRepository,
            BenchmarkResultJpaRepository benchmarkResultJpaRepository,
            BenchmarkModuleScoreJpaRepository benchmarkModuleScoreJpaRepository,
            CalculatorResultJpaRepository calculatorResultJpaRepository,
            SpringDataContactRepository contactRepository,
            SpringDataInvitationRepository invitationRepository) {
        this.operatorJpaRepository = operatorJpaRepository;
        this.evaluationJpaRepository = evaluationJpaRepository;
        this.benchmarkResultJpaRepository = benchmarkResultJpaRepository;
        this.benchmarkModuleScoreJpaRepository = benchmarkModuleScoreJpaRepository;
        this.calculatorResultJpaRepository = calculatorResultJpaRepository;
        this.contactRepository = contactRepository;
        this.invitationRepository = invitationRepository;
    }

    /**
     * Loads all metrics needed for the dashboard summary.
     * The {@code command.from()} and {@code command.to()} fields may be null,
     * in which case queries return all-time statistics.
     */
    @Override
    public MetricsSnapshot load(GetDashboardSummaryCommand command) {
        // Core KPIs
        long totalOperators = operatorJpaRepository.count();
        long evaluationsCompleted = benchmarkResultJpaRepository.countCompleted(
                command.from(), command.to());
        double averageBenchmarkScore = orZero(benchmarkResultJpaRepository.averageScore(
                command.from(), command.to()));
        // PDFs generated: one PDF is produced per completed benchmark evaluation
        long generatedReports = evaluationsCompleted;

        // Extended metrics
        List<DashboardSummary.MaturityDistributionItem> maturityDistribution =
                benchmarkResultJpaRepository.countByMaturityLevel(command.from(), command.to()).stream()
                        .map(item -> new DashboardSummary.MaturityDistributionItem(
                                MaturityLevel.valueOf(item.getLevel()),
                                item.getCount()))
                        .toList();

        List<DashboardSummary.CategoryAverageItem> categoryAverages =
                benchmarkModuleScoreJpaRepository.averageByModule().stream()
                        .map(item -> new DashboardSummary.CategoryAverageItem(
                                BenchmarkModule.valueOf(item.getModule()),
                                item.getAverage()))
                        .toList();

        var calculatorAggregate = calculatorResultJpaRepository.aggregate();

        return new MetricsSnapshot(
                totalOperators,
                evaluationsCompleted,
                averageBenchmarkScore,
                generatedReports,
                contactRepository.count(),
                invitationRepository.countSent(command.campaignId()),
                invitationRepository.countVisited(command.campaignId()),
                evaluationJpaRepository.count(),
                orZero(calculatorAggregate.getAverageUtilization()),
                orZero(calculatorAggregate.getNonProductiveCapacityMw()),
                orZero(calculatorAggregate.getEstimatedAnnualCost()),
                maturityDistribution,
                categoryAverages);
    }

    private double orZero(Double value) {
        return value == null ? 0d : value;
    }
}
