package com.apexgym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trainers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String specialty; // e.g., "Strength & Conditioning", "Yoga"

    @Column(length = 1000)
    private String bio;

    @Column(name = "certifications")
    private String certifications; // e.g., "CSCS, NASM-CPT"

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "clients_trained")
    private Integer clientsTrained;

    @Column(name = "rating")
    private Double rating; // e.g., 4.9

    @Column(name = "is_head_coach")
    private Boolean isHeadCoach = false;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getInitials() {
        return firstName.substring(0, 1).toUpperCase() + lastName.substring(0, 1).toUpperCase();
    }
}