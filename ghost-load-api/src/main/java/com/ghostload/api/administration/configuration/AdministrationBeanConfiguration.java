package com.ghostload.api.administration.configuration;

import com.ghostload.api.administration.application.port.in.AuthenticateAdminUseCase;
import com.ghostload.api.administration.application.port.out.GenerateAdminTokenPort;
import com.ghostload.api.administration.application.port.out.LoadAdminByEmailPort;
import com.ghostload.api.administration.application.port.out.VerifyPasswordPort;
import com.ghostload.api.administration.application.service.AuthenticateAdminService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class AdministrationBeanConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    AuthenticateAdminUseCase authenticateAdminUseCase(
            LoadAdminByEmailPort loadAdminByEmailPort,
            VerifyPasswordPort verifyPasswordPort,
            GenerateAdminTokenPort generateAdminTokenPort) {
        return new AuthenticateAdminService(
                loadAdminByEmailPort,
                verifyPasswordPort,
                generateAdminTokenPort);
    }
}
