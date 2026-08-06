package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.Email;
import com.ghostload.api.assessment.domain.model.Operator;
import com.ghostload.api.assessment.domain.model.OperatorId;

import java.util.Optional;

// Puerto de salida: expresa una NECESIDAD del negocio ("necesito poder buscar
// un operador por email"), no una tecnología. La implementación concreta
// (JPA, PostgreSQL) vive en el adaptador, no acá.
public interface LoadOperatorPort {

    Optional<Operator> findByEmail(Email email);
}
