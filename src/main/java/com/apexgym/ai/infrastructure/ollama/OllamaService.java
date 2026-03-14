package com.apexgym.ai.infrastructure.ollama;

import com.apexgym.dto.OllamaOptions;
import com.apexgym.dto.OllamaRequest;
import com.apexgym.dto.OllamaResponse;
import com.apexgym.dto.ai.ClassRecommendationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Service
@Slf4j
public class OllamaService {

    public static final String OLLAMA_URL = "http://127.0.0.1:11434/api";
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private static final String MODEL = "llama3.2:1b";
    private final WebClient webClient;

    public OllamaService(ObjectMapper objectMapper) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMinutes(2));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(OLLAMA_URL)
                .build();
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder().baseUrl(OLLAMA_URL).build();
    }

    public String getJsonResponse(String prompt) {
        OllamaRequest request = new OllamaRequest(
                MODEL,
                prompt,
                false,
                null,
                -1L,
                new OllamaOptions(0.0)
        );

        return executeRequest(request);
    }

    public String getAiResponse(String systemPrompt, String userPrompt) {
        OllamaRequest request = new OllamaRequest(
                MODEL,
                userPrompt,
                systemPrompt,
                false,
                null,
                -1L,
                new OllamaOptions(0.4)
        );

        return executeRequest(request);
    }

    private String executeRequest(OllamaRequest request) {
        OllamaResponse response = restClient.post()
                .uri("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OllamaResponse.class);

        return (response != null) ? response.response() : "";
    }

    public Flux<String> streamAiResponse(String systemPrompt, String userPrompt) {
        OllamaRequest request = new OllamaRequest(
                MODEL,
                userPrompt,
                systemPrompt,
                true,
                null,
                -1L,
                new OllamaOptions(0.4)
        );

        return webClient.post()
                .uri("/generate")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(String.class)
                .map(line -> {
                    try {
                        return objectMapper.readValue(line, OllamaResponse.class).response();
                    } catch (Exception e) {
                        return "";
                    }
                });
    }

    public Flux<ClassRecommendationDTO> getRecommendationsStream(String systemPrompt, String userFormattedPrompt) {
        OllamaRequest request = new OllamaRequest(
                MODEL,
                userFormattedPrompt,
                systemPrompt,
                true,
                null,
                -1L,
                new OllamaOptions(0.4)
        );
        return webClient.post()
                .uri("/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(ClassRecommendationDTO.class)
                .delayElements(Duration.ofMillis(500));
    }
}
