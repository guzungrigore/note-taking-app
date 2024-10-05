package org.example.calendarservice.repository;

import org.example.calendarservice.entity.EventNoteLink;
import org.example.calendarservice.entity.EventNoteLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventNoteLinkRepository extends JpaRepository<EventNoteLink, EventNoteLinkId> {
    List<EventNoteLink> findByIdEventId(UUID eventId);
}
