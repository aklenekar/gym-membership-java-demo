package com.apexgym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gym_classes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GymClassCategory category;

    @Column(nullable = false)
    private String name; // e.g., "HIIT Bootcamp", "Yoga Flow"

    @Column(name = "instructor_name")
    private String instructorName; // e.g., "Coach Sarah"

    @Column(nullable = false)
    private String location; // e.g., "Studio A", "Main Floor"

    @Column(name = "class_date", nullable = false)
    private LocalDateTime classDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "current_bookings")
    private Integer currentBookings = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    //@ManyToMany(mappedBy = "bookedClasses")
    //private Set<User> bookedUsers = new HashSet<>();

    @Transient
    public boolean hasFreeSpots() {
        return currentBookings < maxCapacity;
    }
}