package com.ghostload.api.administration.adapter.in.web;

public record AdminLoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        AdminSummaryResponse admin) {
}
