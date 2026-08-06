package com.ghostload.api.crm.application.port.in;

import com.ghostload.api.crm.domain.model.CrmPipeline;

import java.util.UUID;

public interface AddPipelineNoteUseCase {

    CrmPipeline addNote(AddPipelineNoteCommand command);

    record AddPipelineNoteCommand(UUID pipelineId, String note) {
    }
}