package com.ghostload.api.administration.application.service;

import com.ghostload.api.administration.application.port.in.GetRecentResponsesQuery;
import com.ghostload.api.administration.application.port.out.LoadCompletedEvaluationsPort;

public final class GetRecentResponsesService implements GetRecentResponsesQuery {

    private final LoadCompletedEvaluationsPort loadCompletedEvaluationsPort;

    public GetRecentResponsesService(LoadCompletedEvaluationsPort loadCompletedEvaluationsPort) {
        this.loadCompletedEvaluationsPort = loadCompletedEvaluationsPort;
    }

    @Override
    public RecentResponsesPage list(RecentResponsesCommand command) {
        return loadCompletedEvaluationsPort.load(
                command.page(),
                command.size(),
                command.from(),
                command.to());
    }
}
