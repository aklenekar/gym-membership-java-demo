package com.apexgym.ai.infrastructure.gemini;

import com.apexgym.ai.dto.ClassRecommendationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class GeminiService {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models";

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.0-flash}")
    private String model;

    private final RestClient restClient;
    private final WebClient webClient;

    public GeminiService() {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMinutes(2));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(BASE_URL)
                .build();
        this.webClient = WebClient.builder().baseUrl(BASE_URL).build();
    }

    public String getJsonResponse(String prompt) {
        return getAiResponse(null, prompt);
    }

    public String getAiResponse(String systemPrompt, String userPrompt) {
        GeminiRequest request = buildRequest(systemPrompt, userPrompt, 0.4);

        GeminiResponse response = restClient.post()
                .uri("/{model}:generateContent?key={key}", model, apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);

        return response != null ? response.extractText() : "";
    }

    public Flux<String> streamAiResponse(String systemPrompt, String userPrompt) {
        GeminiRequest request = buildRequest(systemPrompt, userPrompt, 0.4);

        return webClient.post()
                .uri("/{model}:streamGenerateContent?alt=sse&key={key}", model, apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(GeminiResponse.class)
                .map(GeminiResponse::extractText)
                .filter(text -> text != null && !text.isBlank());
    }

    public Flux<ClassRecommendationDTO> getRecommendationsStream(String systemPrompt, String userPrompt) {
        String response = getAiResponse(systemPrompt, userPrompt);
        List<ClassRecommendationDTO> recommendations = GeminiJsonUtil.parseClassRecommendations(response);
        return Flux.fromIterable(recommendations).delayElements(Duration.ofMillis(500));
    }

    private GeminiRequest buildRequest(String systemPrompt, String userPrompt, double temperature) {
        GeminiContent systemInstruction = systemPrompt != null
                ? GeminiContent.of("user", systemPrompt)
                : null;

        return new GeminiRequest(
                List.of(GeminiContent.of("user", userPrompt)),
                systemInstruction,
                new GeminiGenerationConfig(temperature)
        );
    }
}
