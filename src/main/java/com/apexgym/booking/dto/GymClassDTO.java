package com.apexgym.booking.dto;

import lombok.Builder;

@Builder
public record GymClassDTO(
    Long id,
    String category,
    String name,
    String instructor,
    String location,
    String startTime,
    String durationMin,
    String capacity,
    String booked,
    String spotsInfo,
    Boolean isBooked,
    Long bookingId
) {}
