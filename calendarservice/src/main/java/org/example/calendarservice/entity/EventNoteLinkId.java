package org.example.calendarservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EventNoteLinkId implements java.io.Serializable {

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "note_id", nullable = false, length = 255)
    private UUID noteId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getNoteId() {
        return noteId;
    }

    public void setNoteId(UUID noteId) {
        this.noteId = noteId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventNoteLinkId that)) return false;
        return Objects.equals(eventId, that.eventId) && Objects.equals(noteId, that.noteId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, noteId, userId);
    }

    @Override
    public String toString() {
        return "EventNoteLinkId{" +
                "eventId=" + eventId +
                ", noteId='" + noteId + '\'' +
                ", userId=" + userId +
                '}';
    }
}