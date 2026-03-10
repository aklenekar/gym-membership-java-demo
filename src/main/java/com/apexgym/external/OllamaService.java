package com.apexgym.external;

import com.apexgym.dto.OllamaOptions;
import com.apexgym.dto.OllamaRequest;
import com.apexgym.dto.OllamaResponse;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class OllamaService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper; // To parse JSON chunks
    private static final String MODEL = "llama3.2:1b";
    private final WebClient webClient;

    public OllamaService(ObjectMapper objectMapper) {
        // Explicitly use the JDK-based request factory
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMinutes(2)); // Ollama can be slow
        // Initialize RestClient with the Ollama base URL
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl("http://127.0.0.1:11434/api")
                .defaultHeader("Accept", "application/json")
                .build();
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder().baseUrl("http://127.0.0.1:11434/api").build();
    }

    /**
     * Optimized for JSON responses (like your nutrition plan)
     */
    public String getJsonResponse(String prompt) {
        // We set format to "json" and keep_alive to -1 (infinite)
        OllamaRequest request = new OllamaRequest(
                MODEL,
                prompt,
                false,
                null,
                -1L,
                new OllamaOptions(0.0/*, 1000, 2048*/)
        );

        return executeRequest(request);
    }

    public String getAiResponse(String systemPrompt, String userPrompt) {
        OllamaRequest request = new OllamaRequest(
                MODEL,
                userPrompt,
                systemPrompt, // Adding system prompt support
                false,
                null,         // Not forcing JSON here unless needed
                -1L,
                new OllamaOptions(0.4/*, 1000, 2048*/)
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
        // stream: true is the magic key for Ollama
        OllamaRequest request = new OllamaRequest(
                MODEL,
                userPrompt,
                systemPrompt,
                true, // MUST BE TRUE
                null,
                -1L,
                new OllamaOptions(0.4)
        );

        // We use Flux.create to wrap the synchronous blocking read
        return webClient.post()
                .uri("/generate")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_NDJSON) // Ollama streams are newline-delimited JSON
                .retrieve()
                .bodyToFlux(String.class) // Get raw lines
                .map(line -> {
                    try {
                        return objectMapper.readValue(line, OllamaResponse.class).response();
                    } catch (Exception e) {
                        return "";
                    }
                });
    }
}