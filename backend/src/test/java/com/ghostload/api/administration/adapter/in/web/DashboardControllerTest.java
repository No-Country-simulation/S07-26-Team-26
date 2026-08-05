package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.DashboardSummary;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryQuery;
import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import com.ghostload.api.shared.adapter.in.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardControllerTest {

    @Test
    void shouldReturnFullSummary() throws Exception {
        GetDashboardSummaryQuery query = command -> new DashboardSummary(
                120, 100, 60, 50, 40, 80.0, 72.5, 65.0, 3.2, 12_500.0, 0,
                List.of(new DashboardSummary.MaturityDistributionItem(MaturityLevel.INITIAL, 20)),
                List.of(new DashboardSummary.CategoryAverageItem(BenchmarkModule.AUTOMATION, 70.0)));
        DashboardController controller = new DashboardController(query, new DashboardWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/admin/dashboard/summary")
                        .param("from", "2026-07-01")
                        .param("to", "2026-07-31")
                        .param("campaignId", "0f04b111-7131-49f4-9fb7-9a04705e2309"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactsLoaded").value(120))
                .andExpect(jsonPath("$.invitationsSent").value(100))
                .andExpect(jsonPath("$.linksVisited").value(60))
                .andExpect(jsonPath("$.evaluationsStarted").value(50))
                .andExpect(jsonPath("$.evaluationsCompleted").value(40))
                .andExpect(jsonPath("$.completionRate").value(80.0))
                .andExpect(jsonPath("$.averageBenchmarkScore").value(72.5))
                .andExpect(jsonPath("$.averageUtilization").value(65.0))
                .andExpect(jsonPath("$.accumulatedNonProductiveCapacityMw").value(3.2))
                .andExpect(jsonPath("$.accumulatedEstimatedAnnualCost").value(12500.0))
                .andExpect(jsonPath("$.generatedReports").value(0))
                .andExpect(jsonPath("$.maturityDistribution[0].level").value("INITIAL"))
                .andExpect(jsonPath("$.maturityDistribution[0].count").value(20))
                .andExpect(jsonPath("$.categoryAverages[0].module").value("AUTOMATION"))
                .andExpect(jsonPath("$.categoryAverages[0].score").value(70.0));
    }

    @Test
    void shouldReturnSummaryWithoutOptionalParameters() throws Exception {
        GetDashboardSummaryQuery query = command -> new DashboardSummary(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, List.of(), List.of());
        DashboardController controller = new DashboardController(query, new DashboardWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/admin/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completionRate").value(0));
    }
}
