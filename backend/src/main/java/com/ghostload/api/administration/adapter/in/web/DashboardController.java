package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.GetDashboardSummaryQuery;
import com.ghostload.api.administration.application.port.in.GetRecentResponsesQuery;
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
    private final GetRecentResponsesQuery getRecentResponsesQuery;
    private final DashboardWebMapper mapper;

    public DashboardController(
            GetDashboardSummaryQuery getDashboardSummaryQuery,
            GetRecentResponsesQuery getRecentResponsesQuery,
            DashboardWebMapper mapper) {
        this.getDashboardSummaryQuery = getDashboardSummaryQuery;
        this.getRecentResponsesQuery = getRecentResponsesQuery;
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

    @GetMapping("/recent-responses")
    public ResponseEntity<RecentResponsesPageResponse> recentResponses(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = getRecentResponsesQuery.list(
                mapper.toRecentResponsesCommand(from, to, page, size));
        return ResponseEntity.ok(mapper.toRecentResponsesResponse(result));
    }
}
