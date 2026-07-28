package com.ghostload.api.administration.application.port.out;

import com.ghostload.api.administration.domain.model.AdminUser;

public interface GenerateAdminTokenPort {

    GeneratedAdminToken generate(AdminUser adminUser);
}
