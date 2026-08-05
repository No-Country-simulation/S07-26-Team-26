package com.ghostload.api.administration.application.port.out;

import com.ghostload.api.administration.application.port.in.ListOperatorsQuery;

import java.util.Optional;
import java.util.UUID;

public interface LoadOperatorListPort {

    ListOperatorsQuery.OperatorPage load(int page, int size, String state, String search);

    Optional<ListOperatorsQuery.OperatorListItem> loadDetail(UUID operatorId);
}
