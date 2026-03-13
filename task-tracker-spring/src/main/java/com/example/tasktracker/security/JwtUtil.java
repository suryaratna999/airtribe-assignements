package com.example.tasktracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Arrays;

public class JwtUtil {
    private static final String SECRET_RAW = System.getenv().getOrDefault("JWT_SECRET", "change-this-secret-for-dev-change-this-secret-for-dev-12345");
    // ensure at least 32 bytes for HS256
    private static final byte[] SECRET_BYTES = Arrays.copyOf(SECRET_RAW.getBytes(), Math.max(32, SECRET_RAW.getBytes().length));
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_BYTES);
    private static final long EXP_MS = 1000L * 60 * 60; // 1 hour

    public static String generateToken(String subject, String role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
    }
}
