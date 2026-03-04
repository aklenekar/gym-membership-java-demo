package com.apexgym.dto;

public record OllamaRequestWithSystemAndUserPrompts(String model, String system, String prompt, boolean stream, OllamaOptions options) {}
