package com.ghostload.api.crm.adapter.persistence;

import com.ghostload.api.crm.application.port.out.LoadCrmPipelinePort;
import com.ghostload.api.crm.application.port.out.SaveCrmPipelinePort;
import com.ghostload.api.crm.domain.model.CrmPipeline;
import com.ghostload.api.crm.domain.model.PipelineNote;
import com.ghostload.api.crm.domain.model.PipelineStatus;
import com.ghostload.api.crm.domain.model.PipelineStatusChange;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CrmPersistenceAdapter implements SaveCrmPipelinePort, LoadCrmPipelinePort {

    private final SpringDataPipelineEntryRepository entryRepository;
    private final SpringDataPipelineNoteRepository noteRepository;
    private final SpringDataPipelineStatusChangeRepository changeRepository;

    public CrmPersistenceAdapter(
            SpringDataPipelineEntryRepository entryRepository,
            SpringDataPipelineNoteRepository noteRepository,
            SpringDataPipelineStatusChangeRepository changeRepository) {
        this.entryRepository = entryRepository;
        this.noteRepository = noteRepository;
        this.changeRepository = changeRepository;
    }

    @Override
    @Transactional
    public void save(CrmPipeline pipeline) {
        PipelineEntryJpaEntity entry = entryRepository.findById(pipeline.id())
                .map(existing -> {
                    existing.update(
                            pipeline.companyName(),
                            pipeline.contactName(),
                            pipeline.email(),
                            pipeline.region(),
                            pipeline.benchmarkScore(),
                            pipeline.status(),
                            pipeline.updatedAt());
                    return existing;
                })
                .orElseGet(() -> new PipelineEntryJpaEntity(
                        pipeline.id(),
                        pipeline.companyName(),
                        pipeline.contactName(),
                        pipeline.email(),
                        pipeline.region(),
                        pipeline.benchmarkScore(),
                        pipeline.status(),
                        pipeline.createdAt(),
                        pipeline.updatedAt()));

        boolean isNew = entryRepository.existsById(entry.id()) == false;
        entryRepository.save(entry);

        if (isNew || !pipeline.notes().isEmpty() || !pipeline.history().isEmpty()) {
            noteRepository.deleteByEntryId(pipeline.id());
            changeRepository.deleteByEntryId(pipeline.id());
            noteRepository.saveAll(pipeline.notes().stream()
                    .map(note -> new PipelineNoteJpaEntity(
                            note.id(),
                            pipeline.id(),
                            note.note(),
                            note.createdAt()))
                    .toList());
            changeRepository.saveAll(pipeline.history().stream()
                    .map(change -> new PipelineStatusChangeJpaEntity(
                            change.id(),
                            pipeline.id(),
                            change.fromStatus(),
                            change.toStatus(),
                            change.changedAt()))
                    .toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CrmPipeline> load(UUID pipelineId) {
        return entryRepository.findById(pipelineId).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrmPipeline> query(LoadCrmPipelinePort.CrmPipelineFilter filter) {
        return entryRepository.findAll().stream()
                .filter(entry -> filter.status() == null || entry.status() == filter.status())
                .filter(entry -> filter.region() == null
                        || (entry.region() != null
                        && entry.region().toLowerCase().contains(filter.region().toLowerCase())))
                .filter(entry -> filter.scoreMin() == null
                        || (entry.benchmarkScore() != null
                        && entry.benchmarkScore() >= filter.scoreMin()))
                .filter(entry -> filter.scoreMax() == null
                        || (entry.benchmarkScore() != null
                        && entry.benchmarkScore() <= filter.scoreMax()))
                .sorted(Comparator.comparing(PipelineEntryJpaEntity::updatedAt).reversed())
                .map(this::toDomain)
                .toList();
    }

    private CrmPipeline toDomain(PipelineEntryJpaEntity entry) {
        List<PipelineNote> notes = noteRepository.findAll().stream()
                .filter(note -> note.entryId().equals(entry.id()))
                .sorted(Comparator.comparing(PipelineNoteJpaEntity::createdAt))
                .map(note -> new PipelineNote(note.id(), note.note(), note.createdAt()))
                .toList();
        List<PipelineStatusChange> history = changeRepository.findAll().stream()
                .filter(change -> change.entryId().equals(entry.id()))
                .sorted(Comparator.comparing(PipelineStatusChangeJpaEntity::changedAt))
                .map(change -> new PipelineStatusChange(
                        change.id(),
                        change.fromStatus(),
                        change.toStatus(),
                        change.changedAt()))
                .toList();
        return CrmPipeline.reconstruct(
                entry.id(),
                entry.companyName(),
                entry.contactName(),
                entry.email(),
                entry.region(),
                entry.benchmarkScore(),
                entry.status(),
                entry.createdAt(),
                entry.updatedAt(),
                notes,
                history);
    }
}