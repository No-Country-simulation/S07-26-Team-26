package com.ghostload.api.administration.application.port.out;

import com.ghostload.api.administration.application.port.in.GetRecentResponsesQuery;

import java.time.Instant;

public interface LoadCompletedEvaluationsPort {

    GetRecentResponsesQuery.RecentResponsesPage load(int page, int size, Instant from, Instant to);
}
