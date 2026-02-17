package com.apexgym.external;

import com.apexgym.dto.OllamaRequest;
import com.apexgym.dto.OllamaResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
public class OllamaService {

    private final RestClient restClient;

    public OllamaService() {
        // Initialize RestClient with the Ollama base URL
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434/api")
                .build();
    }

    /**
     * Sends a prompt to the local Ollama instance and returns the string response.
     * @param model The model name (e.g., "llama3.2")
     * @param prompt The user's query
     * @return The AI's response text
     */
    public String getAiResponse(String prompt) {
        OllamaRequest request = new OllamaRequest("llama3.2:1b", prompt, false);

        return Objects.requireNonNull(restClient.post()
                        .uri("/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .body(OllamaResponse.class))
                .response();
    }
}