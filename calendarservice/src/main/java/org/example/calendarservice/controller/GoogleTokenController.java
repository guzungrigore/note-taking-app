package org.example.calendarservice.controller;

import org.example.calendarservice.dto.GoogleTokenDto;
import org.example.calendarservice.entity.GoogleToken;
import org.example.calendarservice.repository.GoogleTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/tokens")
public class GoogleTokenController {

    private final GoogleTokenRepository googleTokenRepository;

    public GoogleTokenController(GoogleTokenRepository googleTokenRepository) {
        this.googleTokenRepository = googleTokenRepository;
    }

    // Endpoint to save/update Google tokens
    @PostMapping("/save")
    public ResponseEntity<String> saveToken(@RequestBody GoogleTokenDto tokenDto) {
        UUID userId = tokenDto.userId();
        GoogleToken token = googleTokenRepository.findByUserId(userId).orElse(new GoogleToken());

        token.setUserId(userId);
        token.setGoogleAccessToken(tokenDto.accessToken());
        token.setGoogleRefreshToken(tokenDto.refreshToken());
        token.setTokenExpiry(tokenDto.expiry());
        token.setCreatedAt(LocalDateTime.now());
        token.setUpdatedAt(LocalDateTime.now());

        googleTokenRepository.save(token);
        return ResponseEntity.ok("Token saved successfully");
    }
}
