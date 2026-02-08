package com.apexgym.service;

import com.apexgym.dto.*;
import com.apexgym.entity.*;
import com.apexgym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MembershipRepository membershipRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ClassBookingRepository classBookingRepository;
    private final GymClassService gymClassService;
    private final ActivityRepository activityRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public DashboardResponseDTO getDashboardData(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return DashboardResponseDTO.builder()
                .membership(getMembershipInfo(user.getId()))
                .stats(getStats(user.getId()))
                .upcomingClasses(getUpcomingClasses(user.getId()))
                .recentActivities(getRecentActivities(user.getId()))
                .goals(getGoals(user.getId()))
                .build();
    }

    private MembershipInfoDTO getMembershipInfo(Long userId) {
        Membership membership = membershipRepository.findByUserId(userId)
                .orElse(null);

        if (membership == null) {
            return MembershipInfoDTO.builder()
                    .plan("No Active Plan")
                    .status("INACTIVE")
                    .build();
        }

        return MembershipInfoDTO.builder()
                .plan(membership.getPlan().name())
                .status(membership.getStatus().name())
                .memberSince(membership.getMemberSince())
                .nextBillingDate(membership.getNextBillingDate())
                .price(membership.getPrice())
                .build();
    }

    private StatsDTO getStats(Long userId) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        // Count workouts this month
        Long workouts = workoutSessionRepository.countByUserIdAndStartTimeAfter(userId, startOfMonth);

        // Sum hours this month
        Long totalMinutes = workoutSessionRepository.sumDurationByUserIdAndStartTimeAfter(userId, startOfMonth);
        double hours = totalMinutes / 60.0;

        // Count completed classes this month
        Long classes = classBookingRepository.countCompletedClassesByUserIdAndDateAfter(userId, startOfMonth);

        // Calculate average goal progress
        List<Goal> activeGoals = goalRepository.findByUserIdAndIsActiveTrueOrderByStartDateDesc(userId);
        int goalProgress = 0;
        if (!activeGoals.isEmpty()) {
            goalProgress = (int) activeGoals.stream()
                    .mapToInt(Goal::getProgressPercentage)
                    .average()
                    .orElse(0);
        }

        return StatsDTO.builder()
                .workouts(workouts)
                .hours(Math.round(hours * 10.0) / 10.0) // Round to 1 decimal
                .classes(classes)
                .goalProgress(goalProgress)
                .build();
    }

    private List<UpcomingClassDTO> getUpcomingClasses(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<GymClass> upcomingClasses = gymClassService.findUpcomingClasses(now);

        // Get user's bookings
        List<ClassBooking> userBookings = classBookingRepository
                .findByUserIdAndGymClass_ClassDateAfterOrderByGymClass_ClassDate(userId, now);

        List<Long> bookedClassIds = userBookings.stream()
                .map(booking -> booking.getGymClass().getId())
                .toList();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        return upcomingClasses.stream()
                .limit(3)
                .map(gymClass -> UpcomingClassDTO.builder()
                        .id(gymClass.getId())
                        .name(gymClass.getName())
                        .instructor("with " + gymClass.getInstructorName())
                        .location(gymClass.getLocation())
                        .time(gymClass.getClassDate().format(timeFormatter))
                        .date(getDateLabel(gymClass.getClassDate()))
                        .isBooked(bookedClassIds.contains(gymClass.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<ActivityDTO> getRecentActivities(Long userId) {
        List<Activity> activities = activityRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);

        return activities.stream()
                .map(activity -> ActivityDTO.builder()
                        .id(activity.getId())
                        .icon(activity.getIcon())
                        .title(activity.getTitle())
                        .time(getTimeAgo(activity.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());
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

    // Helper methods
    private String getDateLabel(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        if (dateTime.toLocalDate().equals(now.toLocalDate())) {
            return "Today";
        } else if (dateTime.toLocalDate().equals(now.plusDays(1).toLocalDate())) {
            return "Tomorrow";
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("MMM d"));
        }
    }

    private String getTimeAgo(LocalDateTime dateTime) {
        Duration duration = Duration.between(dateTime, LocalDateTime.now());

        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + " seconds ago";
        }

        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        }

        long hours = duration.toHours();
        if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        }

        long days = duration.toDays();
        if (days < 7) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        }

        long weeks = days / 7;
        if (weeks < 4) {
            return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";
        }

        return dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
    }
}
