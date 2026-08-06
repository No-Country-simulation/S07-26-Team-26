package com.ghostload.api.administration.application.service;

import com.ghostload.api.administration.application.port.in.GetRecentResponsesQuery;
import com.ghostload.api.administration.application.port.out.LoadCompletedEvaluationsPort;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetRecentResponsesServiceTest {

    private static final UUID OPERATOR_ID =
            UUID.fromString("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa");

    @Test
    void shouldDelegateToPortAndPreservePage() {
        var expected = new GetRecentResponsesQuery.RecentResponsesPage(
                List.of(new GetRecentResponsesQuery.RecentResponsesItem(
                        OPERATOR_ID,
                        "Juan Pérez",
                        "juan@ghostload.local",
                        "Acme SA",
                        UUID.fromString("bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb"),
                        72.5,
                        88.0,
                        MaturityLevel.MANAGED,
                        Instant.parse("2026-07-15T10:30:00Z"))),
                1,
                1,
                0,
                20);
        LoadCompletedEvaluationsPort port =
                (page, size, from, to) -> expected;

        var service = new GetRecentResponsesService(port);
        var command = new GetRecentResponsesQuery.RecentResponsesCommand(
                0,
                20,
                Instant.parse("2026-07-01T00:00:00Z"),
                Instant.parse("2026-08-01T00:00:00Z"));

        var result = service.list(command);

        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(20);
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).fullName()).isEqualTo("Juan Pérez");
        assertThat(result.items().get(0).score()).isEqualTo(72.5);
        assertThat(result.items().get(0).maturityLevel()).isEqualTo(MaturityLevel.MANAGED);
    }
}