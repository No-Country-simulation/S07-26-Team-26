package com.ghostload.api.administration.configuration;

import com.ghostload.api.administration.application.port.in.AuthenticateAdminUseCase;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryQuery;
import com.ghostload.api.administration.application.port.in.ListOperatorsQuery;
import com.ghostload.api.administration.application.port.out.GenerateAdminTokenPort;
import com.ghostload.api.administration.application.port.out.LoadAdminByEmailPort;
import com.ghostload.api.administration.application.port.out.LoadDashboardMetricsPort;
import com.ghostload.api.administration.application.port.out.LoadOperatorListPort;
import com.ghostload.api.administration.application.port.out.VerifyPasswordPort;
import com.ghostload.api.administration.application.service.AuthenticateAdminService;
import com.ghostload.api.administration.application.service.GetDashboardSummaryService;
import com.ghostload.api.administration.application.service.ListOperatorsService;
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

    @Bean
    GetDashboardSummaryQuery getDashboardSummaryQuery(
            LoadDashboardMetricsPort loadDashboardMetricsPort) {
        return new GetDashboardSummaryService(loadDashboardMetricsPort);
    }

    @Bean
    ListOperatorsQuery listOperatorsQuery(
            LoadOperatorListPort loadOperatorListPort) {
        return new ListOperatorsService(loadOperatorListPort);
    }
}
