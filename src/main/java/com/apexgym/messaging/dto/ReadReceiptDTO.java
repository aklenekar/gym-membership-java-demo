package com.apexgym.messaging.dto;

public record ReadReceiptDTO(
        Long conversationId,
        Long readerId
) {}