package com.apexgym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "personal_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String exercise;

    @Column(name = "record_value", nullable = false)
    private String value;

    @Column(nullable = false)
    private String icon;

    @Column(name = "achieved_at", nullable = false)
    private LocalDateTime achievedAt;
}