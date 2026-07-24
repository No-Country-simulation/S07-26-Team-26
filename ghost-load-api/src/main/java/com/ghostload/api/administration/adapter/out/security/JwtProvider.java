package com.ghostload.api.administration.adapter.out.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    // Generamos una firma segura temporal para codificar los tokens
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // El token durará 1 día entero activo (en milisegundos)
    private final long expirationTime = 86400000;

    // Método para crear el token usando el nombre de usuario
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    // Método para validar que el token que nos envían sea real y no haya expirado
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Método para extraer el nombre de usuario de adentro del token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
