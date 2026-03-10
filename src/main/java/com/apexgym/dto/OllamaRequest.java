package com.apexgym.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Combined Request Object
 * keep_alive: "-1" keeps the model in RAM indefinitely
 * format: "json" enables Ollama's high-speed JSON constrained decoding
 */
public record OllamaRequest(
        String model,
        String prompt,
        String system,
        boolean stream,
        String format,
        @JsonProperty("keep_alive") Long keepAlive,
        OllamaOptions options
) {
    // Constructor for simple prompts
    public OllamaRequest(String model, String prompt, boolean stream, String format, Long keepAlive, OllamaOptions options) {
        this(model, prompt, null, stream, format, keepAlive, options);
    }
}