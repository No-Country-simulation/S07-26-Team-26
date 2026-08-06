package com.ghostload.api.crm.application.port.in;

import com.ghostload.api.crm.application.port.out.LoadCrmPipelinePort.CrmPipelineFilter;
import com.ghostload.api.crm.domain.model.CrmPipeline;

import java.util.List;

public interface ListPipelineEntriesUseCase {

    List<CrmPipeline> list(CrmPipelineFilter filter);
}