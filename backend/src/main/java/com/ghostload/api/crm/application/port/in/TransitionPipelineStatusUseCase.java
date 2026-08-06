package com.ghostload.api.crm.application.port.in;

import com.ghostload.api.crm.domain.model.CrmPipeline;
import com.ghostload.api.crm.domain.model.PipelineStatus;

import java.util.UUID;

public interface TransitionPipelineStatusUseCase {

    CrmPipeline transition(TransitionPipelineStatusCommand command);

    record TransitionPipelineStatusCommand(
            UUID pipelineId,
            PipelineStatus status,
            String note) {
    }
}