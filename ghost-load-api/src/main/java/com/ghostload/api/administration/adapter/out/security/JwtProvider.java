package com.ghostload.api.administration.adapter.out.security;

import com.ghostload.api.administration.application.port.out.GenerateAdminTokenPort;
import com.ghostload.api.administration.application.port.out.GeneratedAdminToken;
import com.ghostload.api.administration.configuration.JwtProperties;
import com.ghostload.api.administration.domain.model.AdminUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider implements GenerateAdminTokenPort {

    private final Key key;
    private final long expirationSeconds;
    private final Clock clock;

    public JwtProvider(JwtProperties properties, Clock clock) {
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = properties.expirationSeconds();
        this.clock = clock;
    }

    @Override
    public GeneratedAdminToken generate(AdminUser adminUser) {
        Instant issuedAt = clock.instant();
        String token = Jwts.builder()
                .setSubject(adminUser.email())
                .claim("adminId", adminUser.id().toString())
                .claim("role", adminUser.role().name())
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(issuedAt.plusSeconds(expirationSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return new GeneratedAdminToken(token, expirationSeconds);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }
}
