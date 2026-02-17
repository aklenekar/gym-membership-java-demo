package com.apexgym.dto;

public record OllamaRequest(String model, String prompt, boolean stream) {}
