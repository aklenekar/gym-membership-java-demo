package com.apexgym.staff.service;

import com.apexgym.auth.persistence.Role;
import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.booking.persistence.ClassBookingRepository;
import com.apexgym.booking.persistence.GymClass;
import com.apexgym.booking.persistence.GymClassRepository;
import com.apexgym.profile.persistence.Membership;
import com.apexgym.profile.persistence.MembershipRepository;
import com.apexgym.staff.dto.*;
import com.apexgym.staff.persistence.Trainer;
import com.apexgym.staff.persistence.TrainerRepository;
import com.apexgym.shared.mappers.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final AdminMapper adminMapper;
    private final GymClassRepository gymClassRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final ClassBookingRepository classBookingRepository;

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

    // ============================================================
    // TRAINER LOGIN ENDPOINTS
    // ============================================================

    public TrainerCandidatesResponseDTO getCandidates(String trainerEmail) {
        // Get all active members (candidates)
        List<User> allMembers = userRepository.findAll().stream()
                .filter(u -> u.getRole().equals(Role.USER) && u.getIsActive())
                .collect(Collectors.toList());

        List<TrainerCandidateDTO> candidates = allMembers.stream()
                .map(user -> {
                    Membership membership = membershipRepository.findByUserId(user.getId()).orElse(null);
                    return TrainerCandidateDTO.builder()
                            .userId(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .membershipPlan(membership != null ? membership.getPlan().name() : "NONE")
                            .membershipStatus(membership != null ? membership.getStatus().name() : "INACTIVE")
                            .memberSince(membership != null ? membership.getMemberSince() : null)
                            .nextBillingDate(membership != null ? membership.getNextBillingDate() : null)
                            .gender(user.getGender())
                            .dateOfBirth(user.getDateOfBirth())
                            .isActive(user.getIsActive())
                            .build();
                })
                .collect(Collectors.toList());

        long activeCandidates = candidates.stream()
                .filter(c -> "ACTIVE".equals(c.membershipStatus()))
                .count();

        long inactiveCandidates = candidates.stream()
                .filter(c -> !"ACTIVE".equals(c.membershipStatus()))
                .count();

        return TrainerCandidatesResponseDTO.builder()
                .candidates(candidates)
                .totalCandidates((long) candidates.size())
                .activeCandidates(activeCandidates)
                .inactiveCandidates(inactiveCandidates)
                .build();
    }

    public TrainerClassesResponseDTO getClasses(String trainerEmail) {
        // Get trainer info
        Trainer trainer = trainerRepository.findByUserEmail(trainerEmail)
                .orElseThrow(() -> new RuntimeException("Trainer not found for email: " + trainerEmail));

        // Get all classes taught by this trainer (matched by instructor name)
        List<GymClass> allClasses = gymClassRepository.findByInstructorNameOrderByClassDate(trainer.getFullName());

        LocalDateTime now = LocalDateTime.now();
        long upcomingClasses = allClasses.stream()
                .filter(c -> c.getClassDate().isAfter(now))
                .count();

        long completedClasses = allClasses.stream()
                .filter(c -> c.getClassDate().isBefore(now))
                .count();

        // Convert to DTOs with formatting
        List<TrainerClassDTO> classesDto = allClasses.stream()
                .map(gymClass -> {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

                    return TrainerClassDTO.builder()
                            .id(gymClass.getId())
                            .name(gymClass.getName())
                            .category(gymClass.getCategory().name())
                            .location(gymClass.getLocation())
                            .classDate(gymClass.getClassDate())
                            .durationMinutes(gymClass.getDurationMinutes())
                            .maxCapacity(gymClass.getMaxCapacity())
                            .currentBookings(gymClass.getCurrentBookings())
                            .availableSpots(gymClass.getMaxCapacity() - gymClass.getCurrentBookings())
                            .isActive(gymClass.getIsActive())
                            .formattedDate(gymClass.getClassDate().format(dateFormatter))
                            .formattedTime(gymClass.getClassDate().format(timeFormatter))
                            .build();
                })
                .collect(Collectors.toList());

        // Calculate average capacity utilization
        int avgCapacityUtilization = classesDto.isEmpty() ? 0 : (int) classesDto.stream()
                .mapToDouble(c -> c.maxCapacity() > 0 ? (c.currentBookings() * 100.0 / c.maxCapacity()) : 0)
                .average()
                .orElse(0.0);

        return TrainerClassesResponseDTO.builder()
                .classes(classesDto)
                .totalClasses((long) classesDto.size())
                .upcomingClasses(upcomingClasses)
                .completedClasses(completedClasses)
                .avgCapacityUtilization(avgCapacityUtilization)
                .build();
    }
}

