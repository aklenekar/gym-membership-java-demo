package com.apexgym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_class")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String instructor;

    private String location;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "duration_min", nullable = false)
    private Integer durationMin;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "booked", nullable = false)
    private Integer booked = 0;

    @Transient
    public boolean hasFreeSpots() {
        return booked < capacity;
    }

    @Transient
    public String getSpotsInfo() {
        return booked + "/" + capacity + " spots";
    }
}