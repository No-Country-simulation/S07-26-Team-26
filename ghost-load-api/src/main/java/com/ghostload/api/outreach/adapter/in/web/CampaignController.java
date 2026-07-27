package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.CreateCampaignResult;
import com.ghostload.api.outreach.application.port.in.CreateCampaignUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/campaigns")
public class CampaignController {

    private final CreateCampaignUseCase createCampaignUseCase;
    private final CampaignWebMapper mapper;

    public CampaignController(
            CreateCampaignUseCase createCampaignUseCase,
            CampaignWebMapper mapper) {
        this.createCampaignUseCase = createCampaignUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CampaignResponse> create(
            @Valid @RequestBody CreateCampaignRequest request) {
        CreateCampaignResult result =
                createCampaignUseCase.create(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
    }
}
