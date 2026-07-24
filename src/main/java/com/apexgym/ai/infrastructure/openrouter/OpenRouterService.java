package com.apexgym.ai.infrastructure.openrouter;

import com.apexgym.ai.dto.ChatMessageDTO;
import com.apexgym.ai.dto.openrouter.*;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OpenRouterService {

    private static final String BASE_URL = "https://openrouter.ai/api/v1";
    private static final String MODEL = "nvidia/nemotron-3-ultra-550b-a55b:free";

    private final RestClient restClient;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OpenRouterService(@Value("${openrouter.api.key}") String apiKey, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String getAiResponse(String systemPrompt, String userPrompt) {
        OpenRouterRequest request = new OpenRouterRequest(
                MODEL,
                List.of(
                        new OpenRouterMessage("system", systemPrompt),
                        new OpenRouterMessage("user", userPrompt)
                ),
                0.4,
                false
        );

        OpenRouterResponse response = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OpenRouterResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return "";
        }
        return response.choices().getFirst().message().content();
    }

    public String getJsonResponse(String prompt) {
        return getAiResponse("You are a helpful assistant. Respond only with raw JSON, no markdown.", prompt);
    }

    public Flux<String> streamAiResponse(String systemPrompt, String userPrompt) {
        OpenRouterRequest request = new OpenRouterRequest(
                MODEL,
                List.of(
                        new OpenRouterMessage("system", systemPrompt),
                        new OpenRouterMessage("user", userPrompt)
                ),
                0.4,
                true
        );

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(line -> !line.isBlank() && !"[DONE]".equals(line.trim()))
                .mapNotNull(line -> {
                    try {
                        OpenRouterResponse chunk = objectMapper.readValue(line, OpenRouterResponse.class);
                        if (chunk.choices() == null || chunk.choices().isEmpty()) return null;
                        OpenRouterMessage delta = chunk.choices().getFirst().delta();
                        return delta != null ? delta.content() : null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(content -> !content.isEmpty());
    }


    public Flux<OpenRouterResponse> streamAiResponse(String systemPrompt, List<ChatMessageDTO> history, String userPrompt
            , List<Map<String, Object>> tools) {
        List<OpenRouterMessage> messages = getOpenRouterMessages(systemPrompt, history, userPrompt);

        // 4. Construct the OpenRouterRequest
        OpenRouterRequest request = new OpenRouterRequest(
                MODEL,
                messages,
                tools,
                0.4,
                true
        );

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(line -> !line.isBlank() && !"[DONE]".equals(line.trim()))
                .mapNotNull(line -> {
                    try {
                        return objectMapper.readValue(line, OpenRouterResponse.class);
                    } catch (Exception e) {
                        return null;
                    }
                });
    }

    private static @NonNull List<OpenRouterMessage> getOpenRouterMessages(String systemPrompt, List<ChatMessageDTO> history, String userPrompt) {
        List<OpenRouterMessage> messages = new ArrayList<>();

        // 1. Always start with the System Prompt
        messages.add(new OpenRouterMessage("system", systemPrompt));

        // 2. Append past conversation history in chronological order
        if (history != null && !history.isEmpty()) {
            for (ChatMessageDTO msg : history) {
                // Map internal roles to OpenRouter roles ("user" or "assistant")
                messages.add(new OpenRouterMessage(msg.role(), msg.content()));
            }
        }

        // 3. Append the latest user query at the end
        messages.add(new OpenRouterMessage("user", userPrompt));
        return messages;
    }
}