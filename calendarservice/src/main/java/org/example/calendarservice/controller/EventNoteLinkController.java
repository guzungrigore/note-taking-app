package org.example.calendarservice.controller;

import org.example.calendarservice.dto.MessageDto;
import org.example.calendarservice.entity.EventNoteLink;
import org.example.calendarservice.entity.EventNoteLinkId;
import org.example.calendarservice.service.EventNoteLinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
public class EventNoteLinkController {

    private final EventNoteLinkService eventNoteLinkService;

    public EventNoteLinkController(EventNoteLinkService eventNoteLinkService) {
        this.eventNoteLinkService = eventNoteLinkService;
    }

    // Endpoint to link a note to an event
    @PostMapping("/link")
    public ResponseEntity<MessageDto> linkNoteToEvent(
            @RequestParam("eventId") UUID eventId,
            @RequestParam("noteId") UUID noteId) {

        UUID userId = getAuthenticatedUserId();  // Retrieve the user ID from authentication

        eventNoteLinkService.linkNoteToEvent(userId, eventId, noteId);

        MessageDto messageDto = new MessageDto("Note linked to event successfully");

        return ResponseEntity.ok(messageDto);
    }

    @DeleteMapping("/link")
    public ResponseEntity<String> deleteLink(
            @RequestParam("eventId") UUID eventId,
            @RequestParam("noteId") UUID noteId) {

        UUID userId = getAuthenticatedUserId();

        // Create the composite ID
        EventNoteLinkId linkId = new EventNoteLinkId();
        linkId.setEventId(eventId);
        linkId.setNoteId(noteId);
        linkId.setUserId(userId);

        // Delete the link by ID
        eventNoteLinkService.deleteLinkById(linkId);

        return ResponseEntity.ok("Link deleted successfully");
    }

    @GetMapping("/links/user")
    public ResponseEntity<List<EventNoteLink>> getLinksByUserId() {

        UUID userId = getAuthenticatedUserId();

        List<EventNoteLink> links = eventNoteLinkService.getLinksByUserId(userId);
        return ResponseEntity.ok(links);
    }

    @GetMapping("/links/event")
    public ResponseEntity<List<EventNoteLink>> getLinksByEventId(@RequestParam("eventId") UUID eventId) {
        List<EventNoteLink> links = eventNoteLinkService.getLinksByEventId(eventId);
        return ResponseEntity.ok(links);
    }

    @GetMapping("/links/note")
    public ResponseEntity<List<EventNoteLink>> getLinksByNoteId(@RequestParam("noteId") UUID noteId) {
        List<EventNoteLink> links = eventNoteLinkService.getLinksByNoteId(noteId);
        return ResponseEntity.ok(links);
    }


    // Method to get the user ID from the authenticated context
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());  // Assuming userId is stored as the principal
    }

}
