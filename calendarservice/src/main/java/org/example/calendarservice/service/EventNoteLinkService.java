package org.example.calendarservice.service;

import org.example.calendarservice.entity.Event;
import org.example.calendarservice.entity.EventNoteLink;
import org.example.calendarservice.entity.EventNoteLinkId;
import org.example.calendarservice.repository.EventNoteLinkRepository;
import org.example.calendarservice.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventNoteLinkService {
    private final EventRepository eventRepository;

    private final EventNoteLinkRepository eventNoteLinkRepository;

    public EventNoteLinkService(EventRepository eventRepository, EventNoteLinkRepository eventNoteLinkRepository) {
        this.eventRepository = eventRepository;
        this.eventNoteLinkRepository = eventNoteLinkRepository;
    }

    // Method to link a note to an event
    public void linkNoteToEvent(UUID userId, UUID eventId, UUID noteId) {
        // Find the event by eventId and userId
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();

            // Ensure the event belongs to the current user
            if (!event.getUserId().equals(userId)) {
                throw new RuntimeException("Unauthorized access. The event does not belong to the user.");
            }

            // Create a new composite key for linking the note to the event
            EventNoteLinkId linkId = new EventNoteLinkId();
            linkId.setEventId(eventId);
            linkId.setNoteId(noteId);
            linkId.setUserId(userId);

            // Create the EventNoteLink entity
            EventNoteLink eventNoteLink = new EventNoteLink();
            eventNoteLink.setId(linkId);
            eventNoteLink.setCreatedAt(LocalDateTime.now());

            // Save the link between the event and the note
            eventNoteLinkRepository.save(eventNoteLink);
        } else {
            throw new RuntimeException("Event not found for eventId: " + eventId);
        }
    }

    public void deleteLinkById(EventNoteLinkId linkId) {
        eventNoteLinkRepository.deleteById(linkId);
    }

    public List<EventNoteLink> getLinksByUserId(UUID userId) {
        return eventNoteLinkRepository.findAll()
                .stream()
                .filter(link -> link.getId().getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<EventNoteLink> getLinksByEventId(UUID eventId) {
        return eventNoteLinkRepository.findByIdEventId(eventId);
    }

    public List<EventNoteLink> getLinksByNoteId(UUID noteId) {
        return eventNoteLinkRepository.findAll()
                .stream()
                .filter(link -> link.getId().getNoteId().equals(noteId))
                .collect(Collectors.toList());
    }
}

