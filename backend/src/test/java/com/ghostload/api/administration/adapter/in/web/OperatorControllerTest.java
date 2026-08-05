package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.ListOperatorsQuery;
import com.ghostload.api.administration.application.port.out.LoadOperatorListPort;
import com.ghostload.api.assessment.domain.model.EvaluationState;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import com.ghostload.api.shared.adapter.in.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OperatorControllerTest {

    private static final UUID OPERATOR_ID =
            UUID.fromString("8f744cf4-df09-4dc1-985a-a1bb27f7b25f");

    private static final ListOperatorsQuery.OperatorListItem ITEM =
            new ListOperatorsQuery.OperatorListItem(
                    OPERATOR_ID,
                    "Ana",
                    "Pérez",
                    "ana.perez@empresa.com",
                    "Data Center SA",
                    "IT Manager",
                    UUID.fromString("0f04b111-7131-49f4-9fb7-9a04705e2309"),
                    EvaluationState.BENCHMARK_COMPLETED,
                    Instant.parse("2026-07-10T15:00:00Z"),
                    82.5,
                    MaturityLevel.ADVANCED,
                    Instant.parse("2026-07-20T18:00:00Z"));

    @Test
    void shouldListOperatorsWithPagination() throws Exception {
        LoadOperatorListPort port = new LoadOperatorListPort() {
            @Override
            public ListOperatorsQuery.OperatorPage load(int page, int size, String state, String search) {
                return new ListOperatorsQuery.OperatorPage(
                        List.of(ITEM), 1, 1, page, size);
            }

            @Override
            public Optional<ListOperatorsQuery.OperatorListItem> loadDetail(UUID operatorId) {
                return Optional.empty();
            }
        };
        ListOperatorsQuery query = new com.ghostload.api.administration.application.service.ListOperatorsService(port);
        OperatorController controller = new OperatorController(query, port, new OperatorWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/admin/operators")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].operatorId").value(OPERATOR_ID.toString()))
                .andExpect(jsonPath("$.items[0].fullName").value("Ana Pérez"))
                .andExpect(jsonPath("$.items[0].email").value("ana.perez@empresa.com"))
                .andExpect(jsonPath("$.items[0].companyName").value("Data Center SA"))
                .andExpect(jsonPath("$.items[0].state").value("BENCHMARK_COMPLETED"))
                .andExpect(jsonPath("$.items[0].benchmarkScore").value(82.5))
                .andExpect(jsonPath("$.items[0].maturityLevel").value("ADVANCED"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldReturnNotFoundForMissingDetail() throws Exception {
        LoadOperatorListPort port = new LoadOperatorListPort() {
            @Override
            public ListOperatorsQuery.OperatorPage load(int page, int size, String state, String search) {
                return new ListOperatorsQuery.OperatorPage(
                        List.of(), 0, 0, page, size);
            }

            @Override
            public Optional<ListOperatorsQuery.OperatorListItem> loadDetail(UUID operatorId) {
                return Optional.empty();
            }
        };
        ListOperatorsQuery query = new com.ghostload.api.administration.application.service.ListOperatorsService(port);
        OperatorController controller = new OperatorController(query, port, new OperatorWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/admin/operators/{operatorId}", OPERATOR_ID))
                .andExpect(status().isNotFound());
    }
}
