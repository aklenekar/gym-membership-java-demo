package com.apexgym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name; // e.g., "Workout Frequency", "Group Classes"

    @Column(nullable = false)
    private Integer targetValue; // e.g., 20 workouts

    @Column(nullable = false)
    private Integer currentValue; // e.g., 18 workouts

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status; // IN_PROGRESS, COMPLETED, FAILED

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Integer getProgressPercentage() {
        if (targetValue == 0) return 0;
        return (int) ((currentValue * 100.0) / targetValue);
    }
}