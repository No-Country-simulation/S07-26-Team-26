package com.ghostload.api.crm.application.port.in;

import com.ghostload.api.crm.domain.model.CrmPipeline;

public interface CreatePipelineEntryUseCase {

    CrmPipeline create(CreatePipelineEntryCommand command);

    record CreatePipelineEntryCommand(
            String companyName,
            String contactName,
            String email,
            String region,
            Double benchmarkScore) {
    }
}