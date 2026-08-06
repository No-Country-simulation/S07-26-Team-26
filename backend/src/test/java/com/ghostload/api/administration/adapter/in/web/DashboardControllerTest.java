package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.DashboardSummary;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryQuery;
import com.ghostload.api.administration.application.port.in.GetRecentResponsesQuery;
import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import com.ghostload.api.shared.adapter.in.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardControllerTest {

    private static final UUID OPERATOR_ID =
            UUID.fromString("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa");
    private static final UUID EVALUATION_ID =
            UUID.fromString("bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb");

    @Test
    void shouldReturnFullSummary() throws Exception {
        // DashboardSummary: totalOperators, evaluationsCompleted, averageBenchmarkScore, generatedReports,
        //                   contactsLoaded, invitationsSent, linksVisited, evaluationsStarted,
        //                   completionRate, averageUtilization, accumulatedNonProductiveCapacityMw,
        //                   accumulatedEstimatedAnnualCost, maturityDistribution, categoryAverages
        GetDashboardSummaryQuery query = command -> new DashboardSummary(
                10,       // totalOperators
                40,       // evaluationsCompleted
                72.5,     // averageBenchmarkScore
                40,       // generatedReports
                120,      // contactsLoaded
                100,      // invitationsSent
                60,       // linksVisited
                50,       // evaluationsStarted
                80.0,     // completionRate
                65.0,     // averageUtilization
                3.2,      // accumulatedNonProductiveCapacityMw
                12_500.0, // accumulatedEstimatedAnnualCost
                List.of(new DashboardSummary.MaturityDistributionItem(MaturityLevel.INITIAL, 20)),
                List.of(new DashboardSummary.CategoryAverageItem(BenchmarkModule.AUTOMATION, 70.0)));
        DashboardController controller = new DashboardController(query, command -> null, new DashboardWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/admin/dashboard/summary")
                        .param("from", "2026-07-01")
                        .param("to", "2026-07-31")
                        .param("campaignId", "0f04b111-7131-49f4-9fb7-9a04705e2309"))
                .andExpect(status().isOk())
                // Core KPIs (Requirement 2, AC-1)
                .andExpect(jsonPath("$.totalOperators").value(10))
                .andExpect(jsonPath("$.evaluationsCompleted").value(40))
                .andExpect(jsonPath("$.averageBenchmarkScore").value(72.5))
                .andExpect(jsonPath("$.generatedReports").value(40))
                // Extended metrics
                .andExpect(jsonPath("$.contactsLoaded").value(120))
                .andExpect(jsonPath("$.invitationsSent").value(100))
                .andExpect(jsonPath("$.linksVisited").value(60))
                .andExpect(jsonPath("$.evaluationsStarted").value(50))
                .andExpect(jsonPath("$.completionRate").value(80.0))
                .andExpect(jsonPath("$.averageUtilization").value(65.0))
                .andExpect(jsonPath("$.accumulatedNonProductiveCapacityMw").value(3.2))
                .andExpect(jsonPath("$.accumulatedEstimatedAnnualCost").value(12500.0))
                .andExpect(jsonPath("$.maturityDistribution[0].level").value("INITIAL"))
                .andExpect(jsonPath("$.maturityDistribution[0].count").value(20))
                .andExpect(jsonPath("$.categoryAverages[0].module").value("AUTOMATION"))
                .andExpect(jsonPath("$.categoryAverages[0].score").value(70.0));
    }

    @Test
    void shouldReturnSummaryWithoutOptionalParameters() throws Exception {
        GetDashboardSummaryQuery query = command -> new DashboardSummary(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, List.of(), List.of());        DashboardController controller = new DashboardController(query, command -> null, new DashboardWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/admin/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completionRate").value(0));
    }

    @Test
    void shouldReturnRecentResponsesPage() throws Exception {
        GetRecentResponsesQuery recentResponses = command -> new GetRecentResponsesQuery.RecentResponsesPage(
                List.of(new GetRecentResponsesQuery.RecentResponsesItem(
                        OPERATOR_ID,
                        "Juan Pérez",
                        "juan@ghostload.local",
                        "Acme SA",
                        EVALUATION_ID,
                        72.5,
                        88.0,
                        MaturityLevel.MANAGED,
                        Instant.parse("2026-07-15T10:30:00Z"))),
                1,
                1,
                20,
                1);
        DashboardController controller = new DashboardController(
                command -> null, recentResponses, new DashboardWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/admin/dashboard/recent-responses")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.items[0].fullName").value("Juan Pérez"))
                .andExpect(jsonPath("$.items[0].score").value(72.5))
                .andExpect(jsonPath("$.items[0].percentile").value(88.0))
                .andExpect(jsonPath("$.items[0].maturityLevel").value("MANAGED"));
    }
}
