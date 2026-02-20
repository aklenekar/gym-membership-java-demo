package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerRankingDTO {
    private int rank;
    private String name;
    private Double rating;
    private Long classCount;
    private String imageUrl;
}
