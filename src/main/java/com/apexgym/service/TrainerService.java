package com.apexgym.service;

import com.apexgym.dto.TrainerDTO;
import com.apexgym.dto.TrainersResponseDTO;
import com.apexgym.entity.Trainer;
import com.apexgym.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public TrainersResponseDTO getAllTrainers() {
        List<Trainer> headCoaches = trainerRepository.findByIsHeadCoachTrueAndIsActiveTrue();
        List<Trainer> regularTrainers = trainerRepository.findByIsHeadCoachFalseAndIsActiveTrue();

        return TrainersResponseDTO.builder()
                .headCoaches(headCoaches.stream().map(this::convertToDTO).collect(Collectors.toList()))
                .trainers(regularTrainers.stream().map(this::convertToDTO).collect(Collectors.toList()))
                .build();
    }

    public List<TrainerDTO> getAllTrainersList() {
        List<Trainer> trainers = trainerRepository.findByIsActiveTrueOrderByIsHeadCoachDescYearsExperienceDesc();
        return trainers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public TrainerDTO getTrainerById(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        return convertToDTO(trainer);
    }

    private TrainerDTO convertToDTO(Trainer trainer) {
        return TrainerDTO.builder()
                .id(trainer.getId())
                .fullName(trainer.getFullName())
                .initials(trainer.getInitials())
                .specialty(trainer.getSpecialty())
                .bio(trainer.getBio())
                .certifications(trainer.getCertifications())
                .yearsExperience(trainer.getYearsExperience())
                .clientsTrained(trainer.getClientsTrained())
                .rating(trainer.getRating())
                .isHeadCoach(trainer.getIsHeadCoach())
                .imageUrl(trainer.getImageUrl())
                .email(trainer.getEmail())
                .phone(trainer.getPhone())
                .build();
    }
}