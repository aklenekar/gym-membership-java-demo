package com.apexgym.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OllamaOptions(
        double temperature/*,
        @JsonProperty("num_ctx") int numCtx,
        @JsonProperty("num_predict") int num_predict*/
) {}
