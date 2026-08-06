package com.ghostload.api.crm.application.port.out;

import com.ghostload.api.crm.domain.model.CrmPipeline;
import com.ghostload.api.crm.domain.model.PipelineStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadCrmPipelinePort {

    Optional<CrmPipeline> load(UUID pipelineId);

    List<CrmPipeline> query(CrmPipelineFilter filter);

    record CrmPipelineFilter(
            PipelineStatus status,
            String region,
            Double scoreMin,
            Double scoreMax) {
    }
}