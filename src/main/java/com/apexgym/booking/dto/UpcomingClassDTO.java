package com.apexgym.booking.dto;

import lombok.Builder;

@Builder
public record UpcomingClassDTO(
    Long id,
    String name,
    String instructor,
    String location,
    String time,
    String date,
    Boolean isBooked,
    Long bookingId
) {}
