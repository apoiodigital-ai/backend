package br.com.tucunare.apoiodigital.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Every endpoint in this API is SDK-facing (called by the partner's app, never by an end-user
 * browser), so there is no login form, no CSRF token, and no session — authentication is the
 * x-api-key tenant lookup performed by {@link ApiKeyAuthenticationFilter}. This is what
 * replaces the old {@code exclude = SecurityAutoConfiguration.class} on the main application
 * class, which previously disabled Spring Security altogether and left every controller open
 * to anonymous callers.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final ApiKeyAuthenticationEntryPoint apiKeyAuthenticationEntryPoint;

    public SecurityConfig(
            ApiKeyAuthenticationFilter apiKeyAuthenticationFilter,
            ApiKeyAuthenticationEntryPoint apiKeyAuthenticationEntryPoint
    ) {
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
        this.apiKeyAuthenticationEntryPoint = apiKeyAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Synthesized tutorial audio is fetched by a plain <audio> src, so it can't
                        // carry the x-api-key header; the filename itself is an unguessable UUID.
                        .requestMatchers("/audio/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(handling -> handling.authenticationEntryPoint(apiKeyAuthenticationEntryPoint))
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
