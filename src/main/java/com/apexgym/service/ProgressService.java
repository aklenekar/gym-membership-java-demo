package com.apexgym.service;

import com.apexgym.dto.*;
import com.apexgym.entity.Achievement;
import com.apexgym.entity.Goal;
import com.apexgym.entity.PersonalRecord;
import com.apexgym.entity.User;
import com.apexgym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ClassBookingRepository classBookingRepository;
    private final PersonalRecordRepository personalRecordRepository;
    private final AchievementRepository achievementRepository;

    public ProgressResponseDTO getProgress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ProgressResponseDTO.builder()
                .goals(getGoals(user.getId()))
                .monthlyStats(getMonthlyStats(user.getId()))
                .personalRecords(getPersonalRecords(user.getId()))
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

    private List<PersonalRecordDTO> getPersonalRecords(Long userId) {
        return personalRecordRepository.findByUserIdOrderByAchievedAtDesc(userId)
                .stream()
                .limit(5)
                .map(this::convertToPersonalRecordDTO)
                .collect(Collectors.toList());
    }

    private List<AchievementDTO> getAchievements(Long userId) {
        return achievementRepository.findByUserIdOrderByUnlockedAtDesc(userId)
                .stream()
                .limit(5)
                .map(this::convertToAchievementDTO)
                .collect(Collectors.toList());
    }

    private PersonalRecordDTO convertToPersonalRecordDTO(PersonalRecord record) {
        return PersonalRecordDTO.builder()
                .exercise(record.getExercise())
                .value(record.getValue())
                .date(formatTimeAgo(record.getAchievedAt()))
                .icon(record.getIcon())
                .build();
    }

    private AchievementDTO convertToAchievementDTO(Achievement achievement) {
        return AchievementDTO.builder()
                .name(achievement.getName())
                .badge(achievement.getBadge())
                .unlockedDate(formatTimeAgo(achievement.getUnlockedAt()))
                .build();
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        long days = ChronoUnit.DAYS.between(dateTime, LocalDateTime.now());

        if (days == 0) return "Today";
        if (days == 1) return "1 day ago";
        if (days < 7) return days + " days ago";
        if (days < 14) return "1 week ago";
        if (days < 30) return (days / 7) + " weeks ago";
        if (days < 60) return "1 month ago";

        return (days / 30) + " months ago";
    }
}