package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.GetDashboardSummaryQuery;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class DashboardController {

    private final GetDashboardSummaryQuery getDashboardSummaryQuery;
    private final DashboardWebMapper mapper;

    public DashboardController(
            GetDashboardSummaryQuery getDashboardSummaryQuery,
            DashboardWebMapper mapper) {
        this.getDashboardSummaryQuery = getDashboardSummaryQuery;
        this.mapper = mapper;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> summary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID campaignId) {
        var result = getDashboardSummaryQuery.summarize(mapper.toCommand(from, to, campaignId));
        return ResponseEntity.ok(mapper.toResponse(result));
    }
}
