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
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue
    @Column(name = "event_id", updatable = false, nullable = false)
    private UUID eventId;

    @Column(name = "google_event_id", nullable = false, unique = true)
    private String googleEventId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "event_title", nullable = false)
    private String eventTitle;

    @Column(name = "event_start_time", nullable = false)
    private LocalDateTime eventStartTime;

    @Column(name = "event_end_time", nullable = false)
    private LocalDateTime eventEndTime;

    @Column(name = "event_description")
    private String eventDescription;

    @Column(name = "event_location")
    private String eventLocation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getGoogleEventId() {
        return googleEventId;
    }

    public void setGoogleEventId(String googleEventId) {
        this.googleEventId = googleEventId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(LocalDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public LocalDateTime getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(LocalDateTime eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
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
        if (!(o instanceof Event event)) return false;
        return Objects.equals(eventId, event.eventId) && Objects.equals(googleEventId, event.googleEventId) && Objects.equals(userId, event.userId) && Objects.equals(eventTitle, event.eventTitle) && Objects.equals(eventStartTime, event.eventStartTime) && Objects.equals(eventEndTime, event.eventEndTime) && Objects.equals(eventDescription, event.eventDescription) && Objects.equals(eventLocation, event.eventLocation) && Objects.equals(createdAt, event.createdAt) && Objects.equals(updatedAt, event.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, googleEventId, userId, eventTitle, eventStartTime, eventEndTime, eventDescription, eventLocation, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", googleEventId='" + googleEventId + '\'' +
                ", userId=" + userId +
                ", eventTitle='" + eventTitle + '\'' +
                ", eventStartTime=" + eventStartTime +
                ", eventEndTime=" + eventEndTime +
                ", eventDescription='" + eventDescription + '\'' +
                ", eventLocation='" + eventLocation + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
