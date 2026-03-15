package com.apexgym.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record CreateProfileRequest(
    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,

    @Email(message = "Email must be a valid e‑mail address")
    @NotBlank(message = "Email is required")
    String email,

    @NotBlank(message = "Password is required")
    String password,

    @Pattern(regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone must be a valid international number")
    String phone,

    String dateOfBirth,
    String gender,

    String street,
    String city,
    String state,
    String zipCode,
    String country,

    @JsonProperty("name")
    String emergencyContactName,
    @JsonProperty("emergencyPhone")
    String emergencyContactPhone,
    @JsonProperty("relationship")
    String emergencyContactRelationship,

    String medicalConditions,
    String fitnessGoals
) {}
