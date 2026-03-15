package com.apexgym.tracking.service;

import com.apexgym.admin.dto.StatsDTO;
import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.booking.persistence.ClassBookingRepository;
import com.apexgym.booking.persistence.GymClassCategory;
import com.apexgym.entity.Activity;
import com.apexgym.entity.ActivityType;
import com.apexgym.entity.Goal;
import com.apexgym.entity.WorkoutSession;
import com.apexgym.repository.ActivityRepository;
import com.apexgym.repository.GoalRepository;
import com.apexgym.shared.mappers.WorkoutMapperImpl;
import com.apexgym.tracking.dto.WorkoutDTO;
import com.apexgym.tracking.dto.WorkoutRequest;
import com.apexgym.tracking.dto.WorkoutsResponseDTO;
import com.apexgym.tracking.persistence.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;
    private final ClassBookingRepository classBookingRepository;
    private final GoalRepository goalRepository;
    private final ActivityRepository activityRepository;
    private final WorkoutMapperImpl workoutMapper;

    public WorkoutsResponseDTO getWorkouts(String email, String workout, String day) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<WorkoutSession> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        LocalDateTime currentTime = LocalDateTime.now();

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("user"), user));

        if (workout != null && !workout.isBlank() && !"all".equalsIgnoreCase(workout)) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category")), workout.toLowerCase()));
        }

        if (day != null && !day.isBlank() && !"all".equalsIgnoreCase(day)) {
            LocalDateTime finalStart;
            LocalDateTime finalEnd;
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

            if (finalStart != null) {
                spec = spec.and((root, query, cb) ->
                        cb.between(root.get("startTime"), finalStart, finalEnd)
                );
            }
        }
        Sort sortByStartTime = Sort.by(Sort.Direction.DESC, "startTime");

        List<WorkoutSession> sessions = workoutSessionRepository.findAll(spec, sortByStartTime);

        return WorkoutsResponseDTO.builder()
                .monthlyStats(getMonthlyStats(user.getId()))
                .workouts(sessions.stream().map(workoutMapper::toDTO).collect(Collectors.toList()))
                .build();
    }

    private StatsDTO getMonthlyStats(Long userId) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        Long workouts = workoutSessionRepository.countByUserIdAndStartTimeAfter(userId, startOfMonth);
        Long totalMinutes = workoutSessionRepository.sumDurationByUserIdAndStartTimeAfter(userId, startOfMonth);
        double hours = (totalMinutes != null ? totalMinutes : 0L) / 60.0;
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

    public WorkoutDTO submitWorkoutSession(WorkoutRequest workoutRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LocalDate date = LocalDate.parse(workoutRequest.startDate());
        LocalTime time = LocalTime.parse(workoutRequest.startTime());
        LocalDateTime startTime = LocalDateTime.of(date, time);
        long durationMinutes = Long.parseLong(workoutRequest.durationMinutes());
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        WorkoutSession session = WorkoutSession.builder()
                .user(user)
                .workoutType(GymClassCategory.valueOf(workoutRequest.category()).getType())
                .category(GymClassCategory.valueOf(workoutRequest.category()))
                .startTime(startTime)
                .endTime(endTime)
                .caloriesBurned(workoutRequest.caloriesBurned())
                .notes(workoutRequest.notes())
                .build();
        workoutSessionRepository.save(session);

        Activity activity = Activity.builder()
                .user(user)
                .type(ActivityType.WORKOUT)
                .title("Completed " + GymClassCategory.valueOf(workoutRequest.category()).getType() + " workout")
                .icon("💪")
                .build();
        activityRepository.save(activity);

        return workoutMapper.toDTO(session);
    }
}
