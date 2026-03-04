package com.apexgym.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OllamaOptions(
        @JsonProperty("num_predict") Integer numPredict,
        @JsonProperty("num_ctx") Integer numCtx,
        @JsonProperty("num_gpu") Integer numGpu
) {}
