package org.example.calendarservice.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.example.calendarservice.dto.MessageDto;
import org.example.calendarservice.entity.GoogleToken;
import org.example.calendarservice.repository.GoogleTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/oauth2")
public class OAuth2CallbackController {

    private final GoogleTokenRepository googleTokenRepository;

    public OAuth2CallbackController(GoogleTokenRepository googleTokenRepository) {
        this.googleTokenRepository = googleTokenRepository;
    }

    @Value("${google.client.client-id}")
    private String clientId;

    @Value("${google.client.client-secret}")
    private String clientSecret;

    @Value("${google.client.redirect-uri}")
    private String redirectUri;

    @PostMapping("/callback")
    public ResponseEntity<MessageDto> handleCallback(@RequestParam("code") String code) throws Exception {

        // Exchange the authorization code for an access token
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                httpTransport, jsonFactory, "https://oauth2.googleapis.com/token", clientId, clientSecret, code, redirectUri)
                .execute();

        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();
        Long expiresInSeconds = tokenResponse.getExpiresInSeconds();

        // Retrieve the authenticated user's ID from the JWT token
        UUID userId = getAuthenticatedUserId();

        // Check if a token already exists for this user, otherwise create a new entry
        GoogleToken googleToken = googleTokenRepository.findByUserId(userId)
                .orElse(new GoogleToken());

        // Set token details
        googleToken.setUserId(userId);
        googleToken.setGoogleAccessToken(accessToken);
        googleToken.setGoogleRefreshToken(refreshToken);
        googleToken.setTokenExpiry(LocalDateTime.now().plusSeconds(expiresInSeconds));
        googleToken.setCreatedAt(LocalDateTime.now());
        googleToken.setUpdatedAt(LocalDateTime.now());

        // Save or update the token in the database
        googleTokenRepository.save(googleToken);

        MessageDto messageDto = new MessageDto("Access Token: " + accessToken);

        return ResponseEntity.ok(messageDto);
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }
}
