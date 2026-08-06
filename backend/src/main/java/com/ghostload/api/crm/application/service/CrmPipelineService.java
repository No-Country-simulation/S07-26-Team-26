package com.ghostload.api.crm.application.service;

import com.ghostload.api.crm.application.port.in.AddPipelineNoteUseCase;
import com.ghostload.api.crm.application.port.in.CreatePipelineEntryUseCase;
import com.ghostload.api.crm.application.port.in.GetPipelineEntryUseCase;
import com.ghostload.api.crm.application.port.in.ListPipelineEntriesUseCase;
import com.ghostload.api.crm.application.port.in.TransitionPipelineStatusUseCase;
import com.ghostload.api.crm.application.port.out.LoadCrmPipelinePort;
import com.ghostload.api.crm.application.port.out.SaveCrmPipelinePort;
import com.ghostload.api.crm.domain.exception.PipelineEntryNotFoundException;
import com.ghostload.api.crm.domain.model.CrmPipeline;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
public class CrmPipelineService implements
        CreatePipelineEntryUseCase,
        GetPipelineEntryUseCase,
        ListPipelineEntriesUseCase,
        AddPipelineNoteUseCase,
        TransitionPipelineStatusUseCase {

    private final LoadCrmPipelinePort loadCrmPipelinePort;
    private final SaveCrmPipelinePort saveCrmPipelinePort;
    private final Clock clock;

    public CrmPipelineService(
            LoadCrmPipelinePort loadCrmPipelinePort,
            SaveCrmPipelinePort saveCrmPipelinePort,
            Clock clock) {
        this.loadCrmPipelinePort = loadCrmPipelinePort;
        this.saveCrmPipelinePort = saveCrmPipelinePort;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CrmPipeline create(CreatePipelineEntryCommand command) {
        CrmPipeline pipeline = CrmPipeline.create(
                command.companyName(),
                command.contactName(),
                command.email(),
                command.region(),
                command.benchmarkScore(),
                clock.instant());
        saveCrmPipelinePort.save(pipeline);
        return pipeline;
    }

    @Override
    @Transactional(readOnly = true)
    public CrmPipeline get(UUID pipelineId) {
        return loadCrmPipelinePort.load(pipelineId)
                .orElseThrow(() -> new PipelineEntryNotFoundException(pipelineId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrmPipeline> list(LoadCrmPipelinePort.CrmPipelineFilter filter) {
        return loadCrmPipelinePort.query(filter);
    }

    @Override
    @Transactional
    public CrmPipeline addNote(AddPipelineNoteCommand command) {
        CrmPipeline pipeline = get(command.pipelineId());
        pipeline.addNote(command.note(), clock);
        saveCrmPipelinePort.save(pipeline);
        return pipeline;
    }

    @Override
    @Transactional
    public CrmPipeline transition(TransitionPipelineStatusCommand command) {
        CrmPipeline pipeline = get(command.pipelineId());
        pipeline.transitionTo(command.status(), clock);
        if (command.note() != null && !command.note().isBlank()) {
            pipeline.addNote(command.note(), clock);
        }
        saveCrmPipelinePort.save(pipeline);
        return pipeline;
    }
}