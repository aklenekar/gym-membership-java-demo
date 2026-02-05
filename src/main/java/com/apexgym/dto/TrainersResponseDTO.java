
package com.apexgym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainersResponseDTO {
    private List<TrainerDTO> headCoaches;
    private List<TrainerDTO> trainers;
}