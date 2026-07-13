package com.apexgym.ai.infrastructure.gemini;

import java.util.List;

public record GeminiContent(String role, List<GeminiPart> parts) {
    public static GeminiContent of(String role, String text) {
        return new GeminiContent(role, List.of(new GeminiPart(text)));
    }
}
