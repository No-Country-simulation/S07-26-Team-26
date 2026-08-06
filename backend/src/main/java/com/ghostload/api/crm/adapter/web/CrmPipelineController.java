package com.ghostload.api.crm.adapter.web;

import com.ghostload.api.crm.application.port.in.AddPipelineNoteUseCase;
import com.ghostload.api.crm.application.port.in.CreatePipelineEntryUseCase;
import com.ghostload.api.crm.application.port.in.GetPipelineEntryUseCase;
import com.ghostload.api.crm.application.port.in.ListPipelineEntriesUseCase;
import com.ghostload.api.crm.application.port.in.TransitionPipelineStatusUseCase;
import com.ghostload.api.crm.application.port.out.LoadCrmPipelinePort.CrmPipelineFilter;
import com.ghostload.api.crm.domain.model.CrmPipeline;
import com.ghostload.api.crm.domain.model.PipelineStatus;
import com.ghostload.api.crm.domain.exception.PipelineEntryNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/crm/pipeline")
public class CrmPipelineController {

    private final CreatePipelineEntryUseCase createPipelineEntryUseCase;
    private final GetPipelineEntryUseCase getPipelineEntryUseCase;
    private final ListPipelineEntriesUseCase listPipelineEntriesUseCase;
    private final AddPipelineNoteUseCase addPipelineNoteUseCase;
    private final TransitionPipelineStatusUseCase transitionPipelineStatusUseCase;
    private final CrmPipelineWebMapper mapper;

    public CrmPipelineController(
            CreatePipelineEntryUseCase createPipelineEntryUseCase,
            GetPipelineEntryUseCase getPipelineEntryUseCase,
            ListPipelineEntriesUseCase listPipelineEntriesUseCase,
            AddPipelineNoteUseCase addPipelineNoteUseCase,
            TransitionPipelineStatusUseCase transitionPipelineStatusUseCase,
            CrmPipelineWebMapper mapper) {
        this.createPipelineEntryUseCase = createPipelineEntryUseCase;
        this.getPipelineEntryUseCase = getPipelineEntryUseCase;
        this.listPipelineEntriesUseCase = listPipelineEntriesUseCase;
        this.addPipelineNoteUseCase = addPipelineNoteUseCase;
        this.transitionPipelineStatusUseCase = transitionPipelineStatusUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<PipelineEntryResponse>> list(
            @RequestParam(required = false) PipelineStatus status,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Double scoreMin,
            @RequestParam(required = false) Double scoreMax) {
        List<PipelineEntryResponse> entries =
                listPipelineEntriesUseCase.list(new CrmPipelineFilter(status, region, scoreMin, scoreMax))
                        .stream()
                        .map(mapper::toResponse)
                        .toList();
        return ResponseEntity.ok(entries);
    }

    @PostMapping
    public ResponseEntity<PipelineEntryResponse> create(
            @Valid @RequestBody CreatePipelineEntryRequest request) {
        CrmPipeline pipeline = createPipelineEntryUseCase.create(
                new CreatePipelineEntryUseCase.CreatePipelineEntryCommand(
                        request.companyName(),
                        request.contactName(),
                        request.email(),
                        request.region(),
                        request.benchmarkScore()));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(pipeline));
    }

    @GetMapping("/{pipelineId}")
    public ResponseEntity<PipelineDetailResponse> getDetail(
            @PathVariable UUID pipelineId) {
        CrmPipeline pipeline = getPipelineEntryUseCase.get(pipelineId);
        return ResponseEntity.ok(mapper.toDetailResponse(pipeline));
    }

    @PostMapping("/{pipelineId}/notes")
    public ResponseEntity<PipelineDetailResponse> addNote(
            @PathVariable UUID pipelineId,
            @Valid @RequestBody AddPipelineNoteRequest request) {
        CrmPipeline pipeline = addPipelineNoteUseCase.addNote(
                new AddPipelineNoteUseCase.AddPipelineNoteCommand(pipelineId, request.note()));
        return ResponseEntity.ok(mapper.toDetailResponse(pipeline));
    }

    @PatchMapping("/{pipelineId}/status")
    public ResponseEntity<PipelineDetailResponse> transition(
            @PathVariable UUID pipelineId,
            @Valid @RequestBody TransitionPipelineStatusRequest request) {
        CrmPipeline pipeline = transitionPipelineStatusUseCase.transition(
                new TransitionPipelineStatusUseCase.TransitionPipelineStatusCommand(
                        pipelineId,
                        request.status(),
                        request.note()));
        return ResponseEntity.ok(mapper.toDetailResponse(pipeline));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) PipelineStatus status,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Double scoreMin,
            @RequestParam(required = false) Double scoreMax) {
        List<CrmPipeline> entries =
                listPipelineEntriesUseCase.list(new CrmPipelineFilter(status, region, scoreMin, scoreMax));
        byte[] csv = buildCsv(entries).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"pipeline.csv\"")
                .body(csv);
    }

    private String buildCsv(List<CrmPipeline> entries) {
        StringBuilder csv = new StringBuilder();
        csv.append("Empresa,Contacto,Email,Region,Score,Estado,Notas,Actualizado\n");
        for (CrmPipeline entry : entries) {
            csv.append(escape(entry.companyName())).append(',')
                    .append(escape(entry.contactName())).append(',')
                    .append(escape(entry.email())).append(',')
                    .append(escape(entry.region())).append(',')
                    .append(entry.benchmarkScore() == null ? "" : entry.benchmarkScore()).append(',')
                    .append(escape(entry.status().name())).append(',')
                    .append(entry.notes().size()).append(',')
                    .append(entry.updatedAt() == null ? "" : entry.updatedAt().toString())
                    .append('\n');
        }
        return csv.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")
                ? "\"" + escaped + "\""
                : escaped;
    }
}