package com.apexgym.ai.dto.gemini;

import com.apexgym.ai.dto.AiResponse;

import java.util.List;

public record GeminiResponse(List<GeminiCandidate> candidates) implements AiResponse {

    public String extractText() {
        if (candidates == null || candidates.isEmpty()) return "";
        GeminiContent content = candidates.get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) return "";
        return content.parts().get(0).text();
    }
}