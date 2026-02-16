package com.apexgym.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email must be a valid e‑mail address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone must be a valid international number")
    private String phone;

    private String dateOfBirth;
    private String gender;

    // ---- address -------------------------------------------------
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    // ---- emergency contact ----------------------------------------
    @JsonProperty("name")                 // maps JSON “name” → field emergencyContactName
    private String emergencyContactName;
    @JsonProperty("emergencyPhone")      // maps JSON “emergencyPhone”
    private String emergencyContactPhone;
    @JsonProperty("relationship")         // maps JSON “relationship”
    private String emergencyContactRelationship;

    // ---- health info ---------------------------------------------
    private String medicalConditions;
    private String fitnessGoals;
}
