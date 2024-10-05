package org.example.calendarservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);  // Remove "Bearer " prefix
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(jwtSecret.getBytes())  // Use the correct secret key
                        .parseClaimsJws(token)
                        .getBody();

                // Extract the "id" field from the token
                String userId = claims.get("id", String.class);

                if (userId != null) {
                    // Create an authentication object and set it in the SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

                    // Set the authentication object in the security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new RuntimeException("Invalid user authentication - user ID is null");
                }

            } catch (Exception e) {
                // If the token is invalid, clear the SecurityContext and return 403 Forbidden
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}