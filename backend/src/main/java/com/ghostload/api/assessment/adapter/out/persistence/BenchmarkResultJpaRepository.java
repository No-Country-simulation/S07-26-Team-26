package com.ghostload.api.assessment.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BenchmarkResultJpaRepository extends JpaRepository<BenchmarkResultJpaEntity, UUID> {

    @Query("""
            select avg(b.totalScore)
              from BenchmarkResultJpaEntity b
             where (cast(:from as Instant) is null or b.completedAt >= :from)
               and (cast(:to as Instant) is null or b.completedAt <= :to)
            """)
    Double averageScore(@Param("from") Instant from, @Param("to") Instant to);

    @Query("""
            select b.maturityLevel as level, count(b) as count
              from BenchmarkResultJpaEntity b
             where (cast(:from as Instant) is null or b.completedAt >= :from)
               and (cast(:to as Instant) is null or b.completedAt <= :to)
             group by b.maturityLevel
            """)
    List<MaturityCountProjection> countByMaturityLevel(
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query("""
            select count(b)
              from BenchmarkResultJpaEntity b
             where (cast(:from as Instant) is null or b.completedAt >= :from)
               and (cast(:to as Instant) is null or b.completedAt <= :to)
            """)
    long countCompleted(@Param("from") Instant from, @Param("to") Instant to);

    interface MaturityCountProjection {
        String getLevel();
        long getCount();
    }
}
