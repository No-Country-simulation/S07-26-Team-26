package com.ghostload.api.assessment.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Estos repositorios de Spring Data JPA son un detalle interno del adaptador.
// El resto de la aplicación (el service) nunca los ve directamente -- solo
// conoce los puertos (LoadOperatorPort, SaveOperatorPort, etc.).
public interface OperatorJpaRepository extends JpaRepository<OperatorJpaEntity, UUID> {
    Optional<OperatorJpaEntity> findByEmail(String email);
}
