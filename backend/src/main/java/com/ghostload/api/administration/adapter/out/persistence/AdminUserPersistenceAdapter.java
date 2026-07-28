package com.ghostload.api.administration.adapter.out.persistence;

import com.ghostload.api.administration.application.port.out.LoadAdminByEmailPort;
import com.ghostload.api.administration.domain.model.AdminUser;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminUserPersistenceAdapter implements LoadAdminByEmailPort {

    private final SpringDataAdminUserRepository repository;

    public AdminUserPersistenceAdapter(SpringDataAdminUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<AdminUser> loadByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(this::toDomain);
    }

    private AdminUser toDomain(AdminUserJpaEntity entity) {
        return new AdminUser(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.isActive());
    }
}
