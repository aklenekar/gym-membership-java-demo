package com.apexgym.ai.dto;

public record OllamaRequestWithSystemAndUserPrompts(String model, String system, String prompt, boolean stream, OllamaOptions options) {}
