/*
package com.apexgym.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaManualConfig {

    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi("http://localhost:11434");
    }

    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .withOllamaApi(ollamaApi)
                .withDefaultOptions(OllamaOptions.builder()
                        .withModel("llama3.2")
                        .withTemperature(0.7)
                        .build())
                .build();
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}*/
