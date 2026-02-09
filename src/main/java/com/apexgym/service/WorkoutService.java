package com.apexgym.service;

import com.apexgym.dto.StatsDTO;
import com.apexgym.dto.WorkoutDTO;
import com.apexgym.dto.WorkoutsResponseDTO;
import com.apexgym.entity.Goal;
import com.apexgym.entity.User;
import com.apexgym.entity.WorkoutSession;
import com.apexgym.repository.UserRepository;
import com.apexgym.repository.WorkoutSessionRepository;
import com.apexgym.repository.ClassBookingRepository;
import com.apexgym.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;
    private final ClassBookingRepository classBookingRepository;
    private final GoalRepository goalRepository;

    public WorkoutsResponseDTO getWorkouts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<WorkoutSession> sessions = workoutSessionRepository.findByUserIdOrderByStartTimeDesc(user.getId());

        return WorkoutsResponseDTO.builder()
                .monthlyStats(getMonthlyStats(user.getId()))
                .workouts(sessions.stream().map(this::convertToDTO).collect(Collectors.toList()))
                .build();
    }

    private StatsDTO getMonthlyStats(Long userId) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        Long workouts = workoutSessionRepository.countByUserIdAndStartTimeAfter(userId, startOfMonth);
        Long totalMinutes = workoutSessionRepository.sumDurationByUserIdAndStartTimeAfter(userId, startOfMonth);
        double hours = totalMinutes / 60.0;
        Long classes = classBookingRepository.countCompletedClassesByUserIdAndDateAfter(userId, startOfMonth);
        Long caloriesBurned = workoutSessionRepository.sumCaloriesBurnedByUserIdAndStartTimeAfter(userId, startOfMonth);

        Integer goalProgress = (int) goalRepository.findByUserIdAndIsActiveTrueOrderByStartDateDesc(userId)
                .stream()
                .mapToInt(Goal::getProgressPercentage)
                .average()
                .orElse(0);

        return StatsDTO.builder()
                .workouts(workouts)
                .hours(Math.round(hours * 10.0) / 10.0)
                .classes(classes)
                .caloriesBurned(caloriesBurned)
                .goalProgress(goalProgress)
                .build();
    }

    private WorkoutDTO convertToDTO(WorkoutSession session) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, h:mm a");

        return WorkoutDTO.builder()
                .id(session.getId())
                .category(session.getCategory().name())
                .workoutType(session.getWorkoutType())
                .startTime(session.getStartTime().format(formatter))
                .durationMinutes(session.getDurationMinutes())
                .caloriesBurned(session.getCaloriesBurned())
                .notes(session.getNotes())
                .build();
    }
}