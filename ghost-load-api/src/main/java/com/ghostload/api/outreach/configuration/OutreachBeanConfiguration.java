package com.ghostload.api.outreach.configuration;

import com.ghostload.api.outreach.application.port.in.CreateCampaignUseCase;
import com.ghostload.api.outreach.application.port.in.ImportContactsUseCase;
import com.ghostload.api.outreach.application.port.out.GenerateInvitationTokenPort;
import com.ghostload.api.outreach.application.port.out.LoadCampaignAudiencePort;
import com.ghostload.api.outreach.application.port.out.LoadExistingContactEmailsPort;
import com.ghostload.api.outreach.application.port.out.ParseContactFilePort;
import com.ghostload.api.outreach.application.port.out.SaveCampaignPort;
import com.ghostload.api.outreach.application.port.out.SaveContactImportBatchPort;
import com.ghostload.api.outreach.application.service.CreateCampaignService;
import com.ghostload.api.outreach.application.service.ImportContactsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class OutreachBeanConfiguration {

    @Bean
    ImportContactsUseCase importContactsUseCase(
            ParseContactFilePort parseContactFilePort,
            LoadExistingContactEmailsPort loadExistingContactEmailsPort,
            SaveContactImportBatchPort saveContactImportBatchPort,
            Clock clock) {
        return new ImportContactsService(
                parseContactFilePort,
                loadExistingContactEmailsPort,
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
}
