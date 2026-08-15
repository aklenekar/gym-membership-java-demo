package com.apexgym.personaltraining.service;

import com.apexgym.personaltraining.dto.*;
import com.apexgym.personaltraining.persistence.*;
import com.apexgym.staff.persistence.Trainer;
import com.apexgym.staff.persistence.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerAvailabilityService {

    private final TrainerAvailabilityRepository availabilityRepository;
    private final TrainerRepository trainerRepository;
    private final PTSessionRepository sessionRepository;

    @Transactional
    public AvailabilitySlotDTO addSlot(String trainerEmail, CreateAvailabilityRequest request) {
        Trainer trainer = trainerRepository.findByUserEmail(trainerEmail)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        TrainerAvailability slot = TrainerAvailability.builder()
                .trainer(trainer)
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .isActive(true)
                .build();

        return toDto(availabilityRepository.save(slot));
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlotDTO> getMySlots(String trainerEmail) {
        Trainer trainer = trainerRepository.findByUserEmail(trainerEmail)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        return availabilityRepository.findByTrainerIdAndIsActiveTrue(trainer.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteSlot(String trainerEmail, Long slotId) {
        Trainer trainer = trainerRepository.findByUserEmail(trainerEmail)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        TrainerAvailability slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        if (!slot.getTrainer().getId().equals(trainer.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        slot.setIsActive(false);
        availabilityRepository.save(slot);
    }

    @Transactional(readOnly = true)
    public List<OpenSlotDTO> getOpenSlots(Long trainerId, LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        List<TrainerAvailability> windows = availabilityRepository
                .findByTrainerIdAndDayOfWeekAndIsActiveTrue(trainerId, day);

        List<PTSession> existing = sessionRepository.findOverlapping(
                trainerId, date.atStartOfDay(), date.plusDays(1).atStartOfDay());

        return windows.stream()
                .flatMap(w -> generateSlots(date, w).stream())
                .filter(slot -> existing.stream().noneMatch(s ->
                        s.getStatus() == SessionStatus.SCHEDULED &&
                                s.getScheduledAt().isEqual(slot.start())))
                .collect(Collectors.toList());
    }

    private List<OpenSlotDTO> generateSlots(LocalDate date, TrainerAvailability window) {
        List<OpenSlotDTO> slots = new java.util.ArrayList<>();
        LocalDateTime cursor = LocalDateTime.of(date, window.getStartTime());
        LocalDateTime end = LocalDateTime.of(date, window.getEndTime());
        while (!cursor.plusMinutes(60).isAfter(end)) {
            slots.add(OpenSlotDTO.builder().start(cursor).end(cursor.plusMinutes(60)).build());
            cursor = cursor.plusMinutes(60);
        }
        return slots;
    }

    private AvailabilitySlotDTO toDto(TrainerAvailability a) {
        return AvailabilitySlotDTO.builder()
                .id(a.getId())
                .dayOfWeek(a.getDayOfWeek())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .build();
    }

    @Transactional(readOnly = true)
    public List<NextAvailabilityDTO> getNextAvailabilityForAllTrainers() {
        List<Trainer> trainers = trainerRepository.findByIsActiveTrueOrderByIsHeadCoachDescYearsExperienceDesc();
        LocalDate today = LocalDate.now();

        return trainers.stream()
                .map(trainer -> {
                    LocalDateTime nextSlot = findNextSlot(trainer.getId(), today, 14);
                    return NextAvailabilityDTO.builder()
                            .trainerId(trainer.getId())
                            .trainerName(trainer.getFullName())
                            .initials(trainer.getInitials())
                            .specialty(trainer.getSpecialty())
                            .imageUrl(trainer.getImageUrl())
                            .nextSlot(nextSlot)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private LocalDateTime findNextSlot(Long trainerId, LocalDate fromDate, int daysAhead) {
        for (int i = 0; i < daysAhead; i++) {
            LocalDate date = fromDate.plusDays(i);
            List<OpenSlotDTO> slots = getOpenSlots(trainerId, date);
            LocalDateTime cutoff = i == 0 ? LocalDateTime.now() : date.atStartOfDay();
            Optional<OpenSlotDTO> next = slots.stream()
                    .filter(s -> s.start().isAfter(cutoff))
                    .findFirst();
            if (next.isPresent()) return next.get().start();
        }
        return null;
    }
}