package com.airtribe.surya.capstone.chronos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class ApiKeyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);

    private final String headerName;
    private final String apiKey;
    private final AuthenticationEntryPoint entryPoint;

    public ApiKeyFilter(String headerName, String apiKey, AuthenticationEntryPoint entryPoint) {
        this.headerName = headerName;
        this.apiKey = apiKey;
        this.entryPoint = entryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String provided = request.getHeader(headerName);
        if (provided == null || provided.isBlank()) {
            log.debug("No API key provided in header {}", headerName);
            entryPoint.commence(request, response, null);
            return;
        }

        if (!provided.equals(apiKey)) {
            log.debug("Invalid API key provided");
            entryPoint.commence(request, response, null);
            return;
        }

        // Set a simple authenticated token (no authorities for now)
        Authentication auth = new UsernamePasswordAuthenticationToken("api-key", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
