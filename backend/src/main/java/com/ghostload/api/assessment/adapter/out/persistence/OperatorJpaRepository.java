package com.ghostload.api.assessment.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

// Estos repositorios de Spring Data JPA son un detalle interno del adaptador.
// El resto de la aplicación (el service) nunca los ve directamente -- solo
// conoce los puertos (LoadOperatorPort, SaveOperatorPort, etc.).
public interface OperatorJpaRepository extends JpaRepository<OperatorJpaEntity, UUID> {
    Optional<OperatorJpaEntity> findByEmail(String email);

    @Query("""
            select o.id as operatorId,
                   o.firstName as firstName,
                   o.lastName as lastName,
                   o.email as email,
                   o.companyName as companyName,
                   o.position as position,
                   e.id as evaluationId,
                   e.state as state,
                   e.createdAt as createdAt,
                   r.totalScore as totalScore,
                   r.maturityLevel as maturityLevel,
                   r.completedAt as completedAt
              from OperatorJpaEntity o
              left join EvaluationJpaEntity e on e.operatorId = o.id
              left join BenchmarkResultJpaEntity r on r.evaluationId = e.id
             where (cast(:state as String) is null or e.state = :state)
               and (cast(:search as String) is null
                    or lower(o.firstName) like lower(concat('%', cast(:search as String), '%'))
                    or lower(o.lastName) like lower(concat('%', cast(:search as String), '%'))
                    or lower(o.companyName) like lower(concat('%', cast(:search as String), '%'))
                    or lower(o.email) like lower(concat('%', cast(:search as String), '%')))
             order by e.createdAt desc
            """)
    Page<OperatorListView> findPage(
            @Param("state") String state,
            @Param("search") String search,
            Pageable pageable);

    @Query("""
            select o.id as operatorId,
                   o.firstName as firstName,
                   o.lastName as lastName,
                   o.email as email,
                   o.companyName as companyName,
                   o.position as position,
                   e.id as evaluationId,
                   e.state as state,
                   e.createdAt as createdAt,
                   r.totalScore as totalScore,
                   r.maturityLevel as maturityLevel,
                   r.completedAt as completedAt
              from OperatorJpaEntity o
              left join EvaluationJpaEntity e on e.operatorId = o.id
              left join BenchmarkResultJpaEntity r on r.evaluationId = e.id
             where o.id = :operatorId
            """)
    Optional<OperatorListView> findDetail(@Param("operatorId") UUID operatorId);

    interface OperatorListView {

        UUID getOperatorId();

        String getFirstName();

        String getLastName();

        String getEmail();

        String getCompanyName();

        String getPosition();

        UUID getEvaluationId();

        String getState();

        Instant getCreatedAt();

        Double getTotalScore();

        String getMaturityLevel();

        Instant getCompletedAt();
    }
}
