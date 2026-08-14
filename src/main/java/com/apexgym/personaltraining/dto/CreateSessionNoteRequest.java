package com.apexgym.personaltraining.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSessionNoteRequest(@NotBlank String content) {}