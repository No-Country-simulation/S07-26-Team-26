package com.ghostload.api.administration.application.service;

import com.ghostload.api.administration.application.port.in.ListOperatorsQuery;
import com.ghostload.api.administration.application.port.out.LoadOperatorListPort;

public final class ListOperatorsService implements ListOperatorsQuery {

    private final LoadOperatorListPort loadOperatorListPort;

    public ListOperatorsService(LoadOperatorListPort loadOperatorListPort) {
        this.loadOperatorListPort = loadOperatorListPort;
    }

    @Override
    public OperatorPage list(ListOperatorsCommand command) {
        return loadOperatorListPort.load(
                command.page(),
                command.size(),
                command.state(),
                command.search());
    }
}
