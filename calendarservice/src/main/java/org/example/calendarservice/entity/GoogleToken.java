package org.example.calendarservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "google_tokens")
public class GoogleToken {

    @Id
    @GeneratedValue
    @Column(name = "token_id", updatable = false, nullable = false)
    private UUID tokenId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "google_access_token", nullable = false, length = 512)
    private String googleAccessToken;

    @Column(name = "google_refresh_token", length = 512)
    private String googleRefreshToken;

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    public UUID getTokenId() {
        return tokenId;
    }

    public void setTokenId(UUID tokenId) {
        this.tokenId = tokenId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getGoogleAccessToken() {
        return googleAccessToken;
    }

    public void setGoogleAccessToken(String googleAccessToken) {
        this.googleAccessToken = googleAccessToken;
    }

    public String getGoogleRefreshToken() {
        return googleRefreshToken;
    }

    public void setGoogleRefreshToken(String googleRefreshToken) {
        this.googleRefreshToken = googleRefreshToken;
    }

    public LocalDateTime getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(LocalDateTime tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoogleToken that)) return false;
        return Objects.equals(tokenId, that.tokenId) && Objects.equals(userId, that.userId) && Objects.equals(googleAccessToken, that.googleAccessToken) && Objects.equals(googleRefreshToken, that.googleRefreshToken) && Objects.equals(tokenExpiry, that.tokenExpiry) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenId, userId, googleAccessToken, googleRefreshToken, tokenExpiry, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "GoogleToken{" +
                "tokenId=" + tokenId +
                ", userId=" + userId +
                ", googleAccessToken='" + googleAccessToken + '\'' +
                ", googleRefreshToken='" + googleRefreshToken + '\'' +
                ", tokenExpiry=" + tokenExpiry +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
