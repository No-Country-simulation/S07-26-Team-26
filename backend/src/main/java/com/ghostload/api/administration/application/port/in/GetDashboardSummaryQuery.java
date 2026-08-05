package com.ghostload.api.administration.application.port.in;

public interface GetDashboardSummaryQuery {

    DashboardSummary summarize(GetDashboardSummaryCommand command);
}
