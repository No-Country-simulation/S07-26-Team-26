package com.ghostload.api.crm.domain.exception;

import java.util.UUID;

public class PipelineEntryNotFoundException extends RuntimeException {

    public PipelineEntryNotFoundException(UUID pipelineId) {
        super("No existe la empresa " + pipelineId + " en el pipeline.");
    }
}