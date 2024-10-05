package org.example.calendarservice.repository;

import org.example.calendarservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByUserId(UUID userId);
    Event findByGoogleEventId(String googleEventId);
}
