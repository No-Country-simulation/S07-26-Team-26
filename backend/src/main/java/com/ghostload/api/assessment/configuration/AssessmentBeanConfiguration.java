package com.ghostload.api.assessment.configuration;

import com.ghostload.api.assessment.adapter.out.email.EvaluationInvitationEmailAdapter;
import com.ghostload.api.assessment.application.port.out.SendEvaluationInvitationPort;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@EnableConfigurationProperties(AssessmentEmailProperties.class)
public class AssessmentBeanConfiguration {

    @Bean
    SendEvaluationInvitationPort sendEvaluationInvitationPort(
            JavaMailSenderImpl mailSender,
            AssessmentEmailProperties properties) {
        return new EvaluationInvitationEmailAdapter(mailSender, properties);
    }
}