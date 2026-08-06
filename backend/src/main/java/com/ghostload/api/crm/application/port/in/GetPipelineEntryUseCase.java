package com.ghostload.api.crm.application.port.in;

import com.ghostload.api.crm.domain.model.CrmPipeline;

import java.util.UUID;

public interface GetPipelineEntryUseCase {

    CrmPipeline get(UUID pipelineId);
}