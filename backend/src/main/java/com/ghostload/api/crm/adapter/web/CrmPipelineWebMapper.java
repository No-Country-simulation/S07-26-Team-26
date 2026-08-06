package com.ghostload.api.crm.adapter.web;

import com.ghostload.api.crm.domain.model.CrmPipeline;
import org.springframework.stereotype.Component;

@Component
public class CrmPipelineWebMapper {

    PipelineEntryResponse toResponse(CrmPipeline pipeline) {
        return new PipelineEntryResponse(
                pipeline.id(),
                pipeline.companyName(),
                pipeline.contactName(),
                pipeline.email(),
                pipeline.region(),
                pipeline.benchmarkScore(),
                pipeline.status().name(),
                pipeline.notes().size(),
                pipeline.createdAt(),
                pipeline.updatedAt());
    }

    PipelineDetailResponse toDetailResponse(CrmPipeline pipeline) {
        return new PipelineDetailResponse(
                pipeline.id(),
                pipeline.companyName(),
                pipeline.contactName(),
                pipeline.email(),
                pipeline.region(),
                pipeline.benchmarkScore(),
                pipeline.status().name(),
                pipeline.createdAt(),
                pipeline.updatedAt(),
                pipeline.notes().stream()
                        .map(note -> new PipelineDetailResponse.PipelineNoteResponse(
                                note.id(),
                                note.note(),
                                note.createdAt()))
                        .toList(),
                pipeline.history().stream()
                        .map(change -> new PipelineDetailResponse.PipelineHistoryResponse(
                                change.id(),
                                change.fromStatus().name(),
                                change.toStatus().name(),
                                change.changedAt()))
                        .toList());
    }
}