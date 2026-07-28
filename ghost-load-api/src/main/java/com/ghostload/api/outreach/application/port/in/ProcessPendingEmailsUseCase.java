package com.ghostload.api.outreach.application.port.in;

public interface ProcessPendingEmailsUseCase {

    int processBatch(int maximumItems);
}
