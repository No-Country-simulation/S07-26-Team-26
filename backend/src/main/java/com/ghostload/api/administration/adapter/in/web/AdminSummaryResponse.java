package com.ghostload.api.administration.adapter.in.web;

import java.util.UUID;

public record AdminSummaryResponse(UUID id, String name, String email) {
}
