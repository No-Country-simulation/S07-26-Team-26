package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.CreateCampaignResult;
import com.ghostload.api.outreach.application.port.in.CreateCampaignUseCase;
import com.ghostload.api.outreach.application.port.in.ListCampaignsUseCase;
import com.ghostload.api.outreach.application.port.in.ListCampaignsUseCase.ListCampaignsQuery;
import com.ghostload.api.outreach.application.port.in.SendCampaignCommand;
import com.ghostload.api.outreach.application.port.in.SendCampaignResult;
import com.ghostload.api.outreach.application.port.in.SendCampaignUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/campaigns")
public class CampaignController {

    private final CreateCampaignUseCase createCampaignUseCase;
    private final SendCampaignUseCase sendCampaignUseCase;
    private final ListCampaignsUseCase listCampaignsUseCase;
    private final CampaignWebMapper mapper;

    public CampaignController(
            CreateCampaignUseCase createCampaignUseCase,
            SendCampaignUseCase sendCampaignUseCase,
            ListCampaignsUseCase listCampaignsUseCase,
            CampaignWebMapper mapper) {
        this.createCampaignUseCase = createCampaignUseCase;
        this.sendCampaignUseCase = sendCampaignUseCase;
        this.listCampaignsUseCase = listCampaignsUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CampaignSummaryResponse> list(
            @RequestParam(required = false) String status) {
        return listCampaignsUseCase
                .list(new ListCampaignsQuery(mapper.toStatus(status)))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<CampaignResponse> create(
            @Valid @RequestBody CreateCampaignRequest request) {
        CreateCampaignResult result =
                createCampaignUseCase.create(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
    }

    @PostMapping("/{campaignId}/send")
    public ResponseEntity<CampaignResponse> send(
            @PathVariable UUID campaignId) {
        SendCampaignResult result =
                sendCampaignUseCase.send(new SendCampaignCommand(campaignId));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mapper.toResponse(result));
    }
}
