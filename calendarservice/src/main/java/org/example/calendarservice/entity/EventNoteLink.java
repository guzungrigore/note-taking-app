package org.example.calendarservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "event_note_link")
public class EventNoteLink {

    @EmbeddedId
    private EventNoteLinkId id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public EventNoteLinkId getId() {
        return id;
    }

    public void setId(EventNoteLinkId id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventNoteLink that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt);
    }

    @Override
    public String toString() {
        return "EventNoteLink{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                '}';
    }
}


