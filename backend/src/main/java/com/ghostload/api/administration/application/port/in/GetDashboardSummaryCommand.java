package com.ghostload.api.administration.application.port.in;

import java.time.Instant;
import java.util.UUID;

public record GetDashboardSummaryCommand(
        Instant from,
        Instant to,
        UUID campaignId) {
}
