package com.ghostload.api.crm.domain.model;

import com.ghostload.api.crm.domain.exception.InvalidPipelineTransitionException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CrmPipelineTest {

    private static final Instant NOW = Instant.parse("2026-07-28T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    private CrmPipeline pipeline() {
        return CrmPipeline.create(
                "Data Center Norte SAC",
                "Ana Torres",
                "ana@empresa.com",
                "Lima",
                78.5,
                NOW);
    }

    @Test
    void shouldStartInOutreachPending() {
        CrmPipeline pipeline = pipeline();

        assertThat(pipeline.status()).isEqualTo(PipelineStatus.OUTREACH_PENDING);
        assertThat(pipeline.companyName()).isEqualTo("Data Center Norte SAC");
        assertThat(pipeline.history()).isEmpty();
        assertThat(pipeline.notes()).isEmpty();
    }

    @Test
    void shouldFollowHappyPathToConverted() {
        CrmPipeline pipeline = pipeline();

        pipeline.transitionTo(PipelineStatus.OUTREACH_SENT, CLOCK);
        pipeline.transitionTo(PipelineStatus.MEETING_SCHEDULED, CLOCK);
        pipeline.transitionTo(PipelineStatus.CONVERTED, CLOCK);

        assertThat(pipeline.status()).isEqualTo(PipelineStatus.CONVERTED);
        assertThat(pipeline.history()).hasSize(3);
        assertThat(pipeline.history().get(1).fromStatus())
                .isEqualTo(PipelineStatus.OUTREACH_SENT);
        assertThat(pipeline.history().get(1).toStatus())
                .isEqualTo(PipelineStatus.MEETING_SCHEDULED);
    }

    @Test
    void shouldRejectSkippingFromPendingToMeeting() {
        CrmPipeline pipeline = pipeline();

        assertThatThrownBy(() ->
                pipeline.transitionTo(PipelineStatus.MEETING_SCHEDULED, CLOCK))
                .isInstanceOf(InvalidPipelineTransitionException.class);
        assertThat(pipeline.status()).isEqualTo(PipelineStatus.OUTREACH_PENDING);
        assertThat(pipeline.history()).isEmpty();
    }

    @Test
    void shouldRejectTerminalStateChanges() {
        CrmPipeline pipeline = pipeline();
        pipeline.transitionTo(PipelineStatus.LOST, CLOCK);

        assertThatThrownBy(() ->
                pipeline.transitionTo(PipelineStatus.OUTREACH_PENDING, CLOCK))
                .isInstanceOf(InvalidPipelineTransitionException.class);
        assertThat(pipeline.status()).isEqualTo(PipelineStatus.LOST);
    }

    @Test
    void shouldRejectSameStateTransition() {
        CrmPipeline pipeline = pipeline();

        assertThatThrownBy(() ->
                pipeline.transitionTo(PipelineStatus.OUTREACH_PENDING, CLOCK))
                .isInstanceOf(InvalidPipelineTransitionException.class);
    }

    @Test
    void shouldRecordNotesAndHistory() {
        CrmPipeline pipeline = pipeline();

        pipeline.transitionTo(PipelineStatus.OUTREACH_SENT, CLOCK);
        pipeline.addNote("Se envió la invitación y se programó seguimiento.", CLOCK);

        assertThat(pipeline.notes()).hasSize(1);
        assertThat(pipeline.notes().getFirst().note())
                .isEqualTo("Se envió la invitación y se programó seguimiento.");
        assertThat(pipeline.history()).hasSize(1);
    }
}