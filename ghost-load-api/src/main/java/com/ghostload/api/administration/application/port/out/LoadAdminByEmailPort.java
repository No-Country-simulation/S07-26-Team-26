package com.ghostload.api.administration.application.port.out;

import com.ghostload.api.administration.domain.model.AdminUser;

import java.util.Optional;

public interface LoadAdminByEmailPort {

    Optional<AdminUser> loadByEmail(String email);
}
