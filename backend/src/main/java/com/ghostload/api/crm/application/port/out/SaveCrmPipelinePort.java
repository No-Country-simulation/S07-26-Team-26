package com.ghostload.api.crm.application.port.out;

import com.ghostload.api.crm.domain.model.CrmPipeline;

public interface SaveCrmPipelinePort {

    void save(CrmPipeline pipeline);
}