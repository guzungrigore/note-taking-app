package org.example.calendarservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (stateless API)
                .csrf(AbstractHttpConfigurer::disable)
                // Configure session management (stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Set authorization rules using a lambda
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/tokens/**").permitAll()
                        .requestMatchers("/api/google/authorize").permitAll()
                        .requestMatchers("/oauth2/**").authenticated()
                        .requestMatchers("/api/events/**").authenticated()
                        .requestMatchers("/api/notes/**").authenticated()
                        .requestMatchers("/api/health").permitAll()
                        .anyRequest().authenticated()  // All other routes require authentication
                )
                // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Password encoder bean (optional, if you're handling password-based auth)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}