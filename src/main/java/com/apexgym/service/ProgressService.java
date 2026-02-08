package com.apexgym.service;

import com.apexgym.dto.*;
import com.apexgym.entity.Goal;
import com.apexgym.entity.User;
import com.apexgym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ClassBookingRepository classBookingRepository;
    private final ActivityRepository activityRepository;

    public ProgressResponseDTO getProgress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ProgressResponseDTO.builder()
                .goals(getGoals(user.getId()))
                .monthlyStats(getMonthlyStats(user.getId()))
                .personalRecords(getPersonalRecords())
                .achievements(getAchievements(user.getId()))
                .build();
    }

    private List<GoalDTO> getGoals(Long userId) {
        List<Goal> goals = goalRepository.findByUserIdAndIsActiveTrueOrderByStartDateDesc(userId);

        return goals.stream()
                .map(goal -> GoalDTO.builder()
                        .id(goal.getId())
                        .name(goal.getName())
                        .currentValue(goal.getCurrentValue())
                        .targetValue(goal.getTargetValue())
                        .progressPercentage(goal.getProgressPercentage())
                        .build())
                .collect(Collectors.toList());
    }

    private StatsDTO getMonthlyStats(Long userId) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        Long workouts = workoutSessionRepository.countByUserIdAndStartTimeAfter(userId, startOfMonth);
        Long totalMinutes = workoutSessionRepository.sumDurationByUserIdAndStartTimeAfter(userId, startOfMonth);
        double hours = totalMinutes / 60.0;
        Long classes = classBookingRepository.countCompletedClassesByUserIdAndDateAfter(userId, startOfMonth);

        Integer goalProgress = (int) goalRepository.findByUserIdAndIsActiveTrueOrderByStartDateDesc(userId)
                .stream()
                .mapToInt(Goal::getProgressPercentage)
                .average()
                .orElse(0);

        return StatsDTO.builder()
                .workouts(workouts)
                .hours(Math.round(hours * 10.0) / 10.0)
                .classes(classes)
                .goalProgress(goalProgress)
                .build();
    }

    private List<PersonalRecordDTO> getPersonalRecords() {
        List<PersonalRecordDTO> records = new ArrayList<>();

        records.add(PersonalRecordDTO.builder()
                .exercise("Bench Press")
                .value("225 lbs")
                .date("5 days ago")
                .icon("🏋️")
                .build());

        records.add(PersonalRecordDTO.builder()
                .exercise("Squat")
                .value("315 lbs")
                .date("1 week ago")
                .icon("🦵")
                .build());

        records.add(PersonalRecordDTO.builder()
                .exercise("Deadlift")
                .value("405 lbs")
                .date("2 weeks ago")
                .icon("💪")
                .build());

        records.add(PersonalRecordDTO.builder()
                .exercise("5K Run")
                .value("22:45")
                .date("1 month ago")
                .icon("🏃")
                .build());

        return records;
    }

    private List<AchievementDTO> getAchievements(Long userId) {
        List<AchievementDTO> achievements = new ArrayList<>();

        achievements.add(AchievementDTO.builder()
                .name("100 Workouts")
                .badge("🏆")
                .unlockedDate("2 days ago")
                .build());

        achievements.add(AchievementDTO.builder()
                .name("30 Day Streak")
                .badge("🔥")
                .unlockedDate("1 week ago")
                .build());

        achievements.add(AchievementDTO.builder()
                .name("Strength Master")
                .badge("💪")
                .unlockedDate("2 weeks ago")
                .build());

        return achievements;
    }
}