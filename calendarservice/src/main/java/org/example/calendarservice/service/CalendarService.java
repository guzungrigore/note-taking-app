package org.example.calendarservice.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import org.example.calendarservice.dto.EventDto;
import org.example.calendarservice.entity.Event;
import org.example.calendarservice.entity.GoogleToken;
import org.example.calendarservice.repository.EventRepository;
import org.example.calendarservice.repository.GoogleTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CalendarService {

    private final EventRepository eventRepository;

    private final GoogleTokenRepository googleTokenRepository;

    public CalendarService(EventRepository eventRepository, GoogleTokenRepository googleTokenRepository) {
        this.eventRepository = eventRepository;
        this.googleTokenRepository = googleTokenRepository;
    }

    // Fetch events from Google Calendar
    public List<Event> fetchEventsFromGoogle(UUID userId) {
        GoogleToken googleToken = googleTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Google token not found for user"));

        try {
            // Initialize transport and JSON factory
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            // Build Google Credential
            Credential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .build()
                    .setAccessToken(googleToken.getGoogleAccessToken());

            // Build Calendar service
            Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("Note-Taking App")
                    .build();

            // Fetch events from Google Calendar
            Events googleEvents = service.events().list("primary")
                    .setTimeMin(new DateTime(System.currentTimeMillis()))  // Filter for future events
                    .execute();

            List<com.google.api.services.calendar.model.Event> items = googleEvents.getItems();

            for (com.google.api.services.calendar.model.Event googleEvent : items) {
                // Check if event already exists
                Event existingEvent = eventRepository.findByGoogleEventId(googleEvent.getId());

                if (existingEvent == null) {
                    // Create a new Event entity
                    Event event = new Event();
                    event.setGoogleEventId(googleEvent.getId());
                    event.setUserId(userId);
                    System.out.println("Ada1");
                    event.setEventTitle(googleEvent.getSummary());

                    // Handle the event's start time (all-day event vs. specific time)
                    if (googleEvent.getStart() != null) {
                        if (googleEvent.getStart().getDateTime() != null) {
                            event.setEventStartTime(convertDateTimeToLocalDateTime(googleEvent.getStart().getDateTime()));
                        } else if (googleEvent.getStart().getDate() != null) {
                            // All-day event: use getDate() instead
                            event.setEventStartTime(convertDateTimeToLocalDateTime(googleEvent.getStart().getDate()));
                        }
                    }

                    // Handle the event's end time (all-day event vs. specific time)
                    if (googleEvent.getEnd() != null) {
                        if (googleEvent.getEnd().getDateTime() != null) {
                            event.setEventEndTime(convertDateTimeToLocalDateTime(googleEvent.getEnd().getDateTime()));
                        } else if (googleEvent.getEnd().getDate() != null) {
                            // All-day event: use getDate() instead
                            event.setEventEndTime(convertDateTimeToLocalDateTime(googleEvent.getEnd().getDate()));
                        }
                    }

                    event.setEventDescription(googleEvent.getDescription());
                    event.setEventLocation(googleEvent.getLocation());
                    event.setCreatedAt(LocalDateTime.now());
                    event.setUpdatedAt(LocalDateTime.now());
                    // Save the event to the database
                    eventRepository.save(event);
                }
            }
            // Return the list of events for the user from the database
            return eventRepository.findByUserId(userId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch events from Google Calendar", e);
        }
    }

    // Convert DateTime to LocalDateTime
    private LocalDateTime convertDateTimeToLocalDateTime(DateTime dateTime) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime.getValue()), ZoneId.systemDefault());
    }

    // Convert Date (all-day events) to LocalDateTime (assuming the event starts at midnight)
    private LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
    }

    // Method to create an event in Google Calendar
    public Event createGoogleEvent(UUID userId, EventDto eventDto) {
        GoogleToken googleToken = googleTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Google token not found for user"));

        Credential credential = new GoogleCredential().setAccessToken(googleToken.getGoogleAccessToken())
                .setRefreshToken(googleToken.getGoogleRefreshToken());

        Calendar service = new Calendar.Builder(credential.getTransport(), credential.getJsonFactory(), credential)
                .setApplicationName("YourAppName")
                .build();

        try {
            com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event()
                    .setSummary(eventDto.title())
                    .setLocation(eventDto.location())
                    .setDescription(eventDto.description());

            com.google.api.services.calendar.model.EventDateTime start = new com.google.api.services.calendar.model.EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(eventDto.startTime()))
                    .setTimeZone("UTC");
            googleEvent.setStart(start);

            com.google.api.services.calendar.model.EventDateTime end = new com.google.api.services.calendar.model.EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(eventDto.endTime()))
                    .setTimeZone("UTC");
            googleEvent.setEnd(end);

            com.google.api.services.calendar.model.Event createdGoogleEvent = service.events().insert("primary", googleEvent).execute();

            // Save to database
            Event event = new Event();
            event.setGoogleEventId(createdGoogleEvent.getId());
            event.setUserId(userId);
            event.setEventTitle(createdGoogleEvent.getSummary());
            event.setEventStartTime(convertDateTimeToLocalDateTime(createdGoogleEvent.getStart().getDateTime()));
            event.setEventEndTime(convertDateTimeToLocalDateTime(createdGoogleEvent.getEnd().getDateTime()));
            event.setEventDescription(createdGoogleEvent.getDescription());
            event.setEventLocation(createdGoogleEvent.getLocation());
            event.setCreatedAt(LocalDateTime.now());
            event.setUpdatedAt(LocalDateTime.now());

            return eventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create event in Google Calendar", e);
        }
    }

    // Additional methods for linking notes
    // ...
}
