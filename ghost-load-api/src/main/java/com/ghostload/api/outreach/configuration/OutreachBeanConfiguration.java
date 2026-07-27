package com.ghostload.api.outreach.configuration;

import com.ghostload.api.outreach.application.port.in.CreateCampaignUseCase;
import com.ghostload.api.outreach.application.port.in.ImportContactsUseCase;
import com.ghostload.api.outreach.application.port.in.ProcessPendingEmailsUseCase;
import com.ghostload.api.outreach.application.port.in.SendCampaignUseCase;
import com.ghostload.api.outreach.application.port.in.VerifyEmailConnectionUseCase;
import com.ghostload.api.outreach.application.port.out.ClaimQueuedEmailPort;
import com.ghostload.api.outreach.application.port.out.FinishCampaignDeliveryPort;
import com.ghostload.api.outreach.application.port.out.GenerateInvitationTokenPort;
import com.ghostload.api.outreach.application.port.out.LoadCampaignAudiencePort;
import com.ghostload.api.outreach.application.port.out.LoadCampaignDeliveryPort;
import com.ghostload.api.outreach.application.port.out.LoadExistingContactsPort;
import com.ghostload.api.outreach.application.port.out.ParseContactFilePort;
import com.ghostload.api.outreach.application.port.out.QueueCampaignEmailsPort;
import com.ghostload.api.outreach.application.port.out.RecordEmailDeliveryPort;
import com.ghostload.api.outreach.application.port.out.SaveCampaignPort;
import com.ghostload.api.outreach.application.port.out.SaveContactImportBatchPort;
import com.ghostload.api.outreach.application.port.out.SendEmailPort;
import com.ghostload.api.outreach.application.port.out.VerifyEmailConnectionPort;
import com.ghostload.api.outreach.application.service.CreateCampaignService;
import com.ghostload.api.outreach.application.service.ImportContactsService;
import com.ghostload.api.outreach.application.service.ProcessPendingEmailsService;
import com.ghostload.api.outreach.application.service.SendCampaignService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(OutreachEmailProperties.class)
public class OutreachBeanConfiguration {

    @Bean
    ImportContactsUseCase importContactsUseCase(
            ParseContactFilePort parseContactFilePort,
            LoadExistingContactsPort loadExistingContactsPort,
            SaveContactImportBatchPort saveContactImportBatchPort,
            Clock clock) {
        return new ImportContactsService(
                parseContactFilePort,
                loadExistingContactsPort,
                saveContactImportBatchPort,
                clock);
    }

    @Bean
    CreateCampaignUseCase createCampaignUseCase(
            LoadCampaignAudiencePort loadCampaignAudiencePort,
            GenerateInvitationTokenPort generateInvitationTokenPort,
            SaveCampaignPort saveCampaignPort,
            Clock clock) {
        return new CreateCampaignService(
                loadCampaignAudiencePort,
                generateInvitationTokenPort,
                saveCampaignPort,
                clock);
    }

    @Bean
    SendCampaignUseCase sendCampaignUseCase(
            LoadCampaignDeliveryPort loadCampaignDeliveryPort,
            QueueCampaignEmailsPort queueCampaignEmailsPort,
            Clock clock) {
        return new SendCampaignService(
                loadCampaignDeliveryPort,
                queueCampaignEmailsPort,
                clock);
    }

    @Bean
    ProcessPendingEmailsUseCase processPendingEmailsUseCase(
            ClaimQueuedEmailPort claimQueuedEmailPort,
            SendEmailPort sendEmailPort,
            RecordEmailDeliveryPort recordEmailDeliveryPort,
            FinishCampaignDeliveryPort finishCampaignDeliveryPort,
            Clock clock) {
        return new ProcessPendingEmailsService(
                claimQueuedEmailPort,
                sendEmailPort,
                recordEmailDeliveryPort,
                finishCampaignDeliveryPort,
                clock);
    }

    @Bean
    VerifyEmailConnectionUseCase verifyEmailConnectionUseCase(
            VerifyEmailConnectionPort verifyEmailConnectionPort) {
        return () -> {
            var diagnostic = verifyEmailConnectionPort.verifyConnection();
            return new VerifyEmailConnectionUseCase.EmailConnectionTestResult(
                    diagnostic.available(),
                    diagnostic.code(),
                    diagnostic.message());
        };
    }
}
