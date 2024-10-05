package org.example.calendarservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GoogleTokenDto(
        UUID userId,
        String accessToken,
        String refreshToken,
        LocalDateTime expiry
) {
}
