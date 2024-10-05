package org.example.calendarservice.controller;

import org.example.calendarservice.dto.EventDto;
import org.example.calendarservice.entity.Event;
import org.example.calendarservice.service.CalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final CalendarService calendarService;

    public EventController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    // Endpoint to fetch events from Google Calendar
    @GetMapping("/fetch")
    public ResponseEntity<List<Event>> fetchEvents(@RequestHeader("Authorization") String authHeader) {
        UUID userId = extractUserIdFromAuthHeader(authHeader);
        List<Event> events = calendarService.fetchEventsFromGoogle(userId);
        return ResponseEntity.ok(events);
    }


    // Endpoint to create a new event in Google Calendar
    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestHeader("Authorization") String authHeader,
                                             @RequestBody EventDto eventDto) {
        UUID userId = extractUserIdFromAuthHeader(authHeader);
        Event createdEvent = calendarService.createGoogleEvent(userId, eventDto);
        return ResponseEntity.ok(createdEvent);
    }

    // Helper method to extract user_id from auth token
    private UUID extractUserIdFromAuthHeader(String authHeader) {
        // Get the authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            // Check if the principal is a String and convert it to UUID if it's the user ID
            if (principal instanceof String) {
                try {
                    // Assuming the principal is the user_id in String format
                    return UUID.fromString((String) principal);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid user authentication - Unable to parse user ID as UUID", e);
                }
            }
        }

        throw new RuntimeException("Invalid user authentication - No valid principal found");
    }

    // Additional endpoints like creating events, linking notes, etc.
    // ...
}