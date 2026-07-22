package com.apexgym.staff.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record TrainerCandidatesResponseDTO(
    List<TrainerCandidateDTO> candidates,
    Long totalCandidates,
    Long activeCandidates,
    Long inactiveCandidates
) {}

