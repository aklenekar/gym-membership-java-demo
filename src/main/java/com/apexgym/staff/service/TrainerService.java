package com.apexgym.staff.service;

import com.apexgym.staff.dto.TrainerDTO;
import com.apexgym.staff.dto.TrainersResponseDTO;
import com.apexgym.staff.persistence.Trainer;
import com.apexgym.staff.persistence.TrainerRepository;
import com.apexgym.shared.mappers.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final AdminMapper adminMapper;

    public TrainersResponseDTO getAllTrainers() {
        List<Trainer> headCoaches = trainerRepository.findByIsHeadCoachTrueAndIsActiveTrue();
        List<Trainer> regularTrainers = trainerRepository.findByIsHeadCoachFalseAndIsActiveTrue();

        return TrainersResponseDTO.builder()
                .headCoaches(headCoaches.stream().map(adminMapper::toTrainerDTO).collect(Collectors.toList()))
                .trainers(regularTrainers.stream().map(adminMapper::toTrainerDTO).collect(Collectors.toList()))
                .build();
    }

    public List<TrainerDTO> getAllTrainersList() {
        List<Trainer> trainers = trainerRepository.findByIsActiveTrueOrderByIsHeadCoachDescYearsExperienceDesc();
        return trainers.stream().map(adminMapper::toTrainerDTO).collect(Collectors.toList());
    }

    public TrainerDTO getTrainerById(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        return adminMapper.toTrainerDTO(trainer);
    }
}
