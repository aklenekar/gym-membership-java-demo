package com.apexgym.ai.dto.ollama;

public record OllamaRequestWithSystemAndUserPrompts(String model, String system, String prompt, boolean stream, OllamaOptions options) {}
