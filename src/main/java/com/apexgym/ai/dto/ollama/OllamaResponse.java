package com.apexgym.ai.dto.ollama;

import com.apexgym.ai.dto.AiResponse;

public record OllamaResponse(String response, Boolean done) implements AiResponse {}
