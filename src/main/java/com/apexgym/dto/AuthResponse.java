package com.apexgym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long expiresIn; // in milliseconds
    private String email;
    private String role;
    private String message;

    public AuthResponse(String token, Long expiresIn, String email, String role) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.email = email;
        this.role = role;
        this.message = "Login successful";
    }
}