package com.apexgym.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
    String token,
    String type,
    Long expiresIn,
    String email,
    String role,
    String message
) {
    public AuthResponse(String token, Long expiresIn, String email, String role) {
        this(token, "Bearer", expiresIn, email, role, "Login successful");
    }
}