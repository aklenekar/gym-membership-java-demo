package com.apexgym.service;

import com.apexgym.dto.StatsDTO;
import com.apexgym.dto.WorkoutDTO;
import com.apexgym.dto.WorkoutsResponseDTO;
import com.apexgym.entity.Goal;
import com.apexgym.entity.GymClass;
import com.apexgym.entity.User;
import com.apexgym.entity.WorkoutSession;
import com.apexgym.repository.UserRepository;
import com.apexgym.repository.WorkoutSessionRepository;
import com.apexgym.repository.ClassBookingRepository;
import com.apexgym.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public WorkoutsResponseDTO getWorkouts(String email, String workout, String day) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<WorkoutSession> spec = Specification.where(null);
        LocalDateTime currentTime = LocalDateTime.now();

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("user"), user));

        if (workout != null && !workout.isBlank() && !"all".equalsIgnoreCase(workout)) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category")), workout.toLowerCase()));
        }

        if (day != null && !day.isBlank() && !"all".equalsIgnoreCase(day)) {
            // 1. Declare variables that will be assigned once
            LocalDateTime finalStart;
            LocalDateTime finalEnd;
            // Use temporary variables for calculation if needed,
            // or just ensure the paths below only assign the variables ONCE.
            switch (day.toLowerCase()) {
                case "today" -> {
                    finalStart = currentTime.with(LocalTime.MIN);
                    finalEnd = finalStart.with(LocalTime.MAX);
                }
                case "tomorrow" -> {
                    finalStart = currentTime.plusDays(1).with(LocalTime.MIN);
                    finalEnd = finalStart.with(LocalTime.MAX);
                }
                case "week" -> {
                    finalStart = currentTime.with(LocalTime.MIN);
                    finalEnd = finalStart.plusDays(7).with(LocalTime.MAX);
                }
                default -> {
                    finalStart = null;
                    finalEnd = null;
                }
            }

            // 2. Because finalStart and finalEnd are assigned exactly once,
            // they are "effectively final" and safe for the Lambda.
            if (finalStart != null) {
                spec = spec.and((root, query, cb) ->
                        cb.between(root.get("startTime"), finalStart, finalEnd)
                );
            }
        }

        List<WorkoutSession> sessions = workoutSessionRepository.findAll(spec);

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