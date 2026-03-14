package com.apexgym.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record TrainersResponseDTO(
    List<TrainerDTO> headCoaches,
    List<TrainerDTO> trainers
) {}