package com.ghostload.api.outreach.adapter.in.web;

public record ImportIssueResponse(
        long row,
        String email,
        String code,
        String message) {
}
