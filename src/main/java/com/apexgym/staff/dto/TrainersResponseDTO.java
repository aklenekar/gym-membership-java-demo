package com.apexgym.staff.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record TrainersResponseDTO(
    List<TrainerDTO> headCoaches,
    List<TrainerDTO> trainers
) {}
