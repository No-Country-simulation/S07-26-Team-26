package com.ghostload.api.outreach.application.port.out;

public record ContactFileRow(
        long rowNumber,
        String firstName,
        String lastName,
        String email,
        String companyName,
        String position) {
}
