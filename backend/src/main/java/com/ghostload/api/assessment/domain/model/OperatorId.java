package com.ghostload.api.assessment.domain.model;

import java.util.UUID;

// Envolvemos el UUID en su propio tipo para que el compilador no nos deje
// confundir por accidente un OperatorId con un EvaluationId, aunque los dos
// sean UUID por dentro. Esto evita bugs de "pasé el id equivocado".
public record OperatorId(UUID value) {

    public static OperatorId newId() {
        return new OperatorId(UUID.randomUUID());
    }

    public static OperatorId of(UUID value) {
        return new OperatorId(value);
    }
}
