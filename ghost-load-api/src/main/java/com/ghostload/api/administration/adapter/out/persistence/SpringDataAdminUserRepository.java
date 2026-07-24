package com.ghostload.api.administration.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataAdminUserRepository extends JpaRepository<AdminUserJpaEntity, UUID> {

    Optional<AdminUserJpaEntity> findByEmailIgnoreCase(String email);
}
