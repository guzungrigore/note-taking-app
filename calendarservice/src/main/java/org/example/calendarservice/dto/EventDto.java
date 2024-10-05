package org.example.calendarservice.dto;

public record EventDto(
        String title,
        String location,
        String description,
        String startTime,
        String endTime
) {
}
