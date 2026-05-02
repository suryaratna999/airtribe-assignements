package com.airtribe.surya.capstone.chronos.config;

import com.airtribe.surya.capstone.chronos.security.ApiKeyFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Value("${api.key:}")
    private String apiKey;

    @Value("${api.key.header:X-API-KEY}")
    private String apiKeyHeader;

    @Bean
    public ApiKeyAuthEntryPoint apiKeyAuthEntryPoint() {
        return new ApiKeyAuthEntryPoint();
    }

    @Bean
    public ApiKeyFilter apiKeyFilter() {
        return new ApiKeyFilter(apiKeyHeader, apiKey, apiKeyAuthEntryPoint());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            // If api key isn't set, keep endpoints open but log a warning
            System.out.println("WARNING: api.key is not configured. Endpoints will not be protected.");
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(apiKeyFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(apiKeyAuthEntryPoint()))
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

        return http.build();
    }
}
