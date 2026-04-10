package com.example.recipeapi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = System.getProperty("security.jwt.secret", "changeit-please-set-a-secure-secret");
    private static final long EXP_SECONDS = Long.parseLong(System.getProperty("security.jwt.expirationSeconds", "3600"));

    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(String subject, String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXP_SECONDS * 1000);
        return Jwts.builder()
                .setSubject(subject)
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static io.jsonwebtoken.Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
