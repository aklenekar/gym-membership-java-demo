package com.apexgym.dto.ai;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String conversationId;
}
