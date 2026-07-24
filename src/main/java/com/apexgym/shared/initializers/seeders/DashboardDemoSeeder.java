package com.apexgym.shared.initializers.seeders;

import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.booking.persistence.*;
import com.apexgym.profile.persistence.*;
import com.apexgym.tracking.persistence.ClassBooking;
import com.apexgym.tracking.persistence.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DashboardDemoSeeder {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final GymClassRepository gymClassRepository;
    private final ClassBookingRepository classBookingRepository;
    private final ActivityRepository activityRepository;
    private final GoalRepository goalRepository;

    public void seed() {
        log.info("Initializing dashboard demo data...");

        User user = userRepository.findByEmail("user@apexgym.com")
                .orElseGet(() -> {
                    log.warn("Demo user user@apexgym.com not found, skipping dashboard initialization.");
                    return null;
                });

        if (user == null) return;

        // Create Membership
        if (membershipRepository.findByUserId(user.getId()).isEmpty()) {
            Membership membership = Membership.builder()
                    .user(user)
                    .plan(MembershipPlan.PRO)
                    .status(MembershipStatus.ACTIVE)
                    .memberSince(LocalDate.now().minusMonths(6))
                    .nextBillingDate(LocalDate.now().plusDays(15))
                    .price(59.0)
                    .autoRenew(true)
                    .build();
            membershipRepository.save(membership);
            log.info("Created membership for demo user");
        }

        // Create Workout Sessions
        if (workoutSessionRepository.findByUserIdOrderByStartTimeDesc(user.getId()).isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < 18; i++) {
                String type = i % 3 == 0 ? "Upper Body" : i % 3 == 1 ? "Cardio" : "HIIT";
                WorkoutSession session = WorkoutSession.builder()
                        .user(user)
                        .workoutType(type)
                        .category(i % 3 == 0 ? GymClassCategory.Strength : i % 3 == 1 ? GymClassCategory.Cardio : GymClassCategory.HIIT)
                        .startTime(now.minusDays(i).minusHours(2))
                        .endTime(now.minusDays(i).minusMinutes(30))
                        .caloriesBurned(450 + (i * 10))
                        .notes("Auto-generated session: " + type)
                        .build();
                workoutSessionRepository.save(session);
            }
            log.info("Created 18 workout sessions");
        }

        // Book first class for user
        gymClassRepository.findAll().stream().findFirst().ifPresent(gymClass -> {
            if (classBookingRepository.findByUserId(user.getId()).isEmpty()) {
                ClassBooking booking = ClassBooking.builder()
                        .user(user)
                        .category(GymClassCategory.HIIT.name())
                        .gymClass(gymClass)
                        .status(BookingStatus.BOOKED)
                        .build();
                classBookingRepository.save(booking);
                log.info("Booked class for demo user");
            }
        });

        // Create Activities
        if (activityRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            activityRepository.save(Activity.builder().user(user).type(ActivityType.WORKOUT).title("Completed Upper Body Workout").icon("🏋️").createdAt(now.minusHours(2)).build());
            activityRepository.save(Activity.builder().user(user).type(ActivityType.CLASS_ATTENDED).title("Attended HIIT Bootcamp").icon("🔥").createdAt(now.minusDays(1)).build());
            activityRepository.save(Activity.builder().user(user).type(ActivityType.ACHIEVEMENT).title("Achieved: 10 Workouts This Month").icon("🏆").createdAt(now.minusDays(2)).build());
            activityRepository.save(Activity.builder().user(user).type(ActivityType.CLASS_ATTENDED).title("Completed Yoga Flow Session").icon("🧘").createdAt(now.minusDays(3)).build());

            log.info("Created demo activities");
        }

        // Create Goals
        if (goalRepository.findByUserIdAndIsActiveTrueOrderByStartDateDesc(user.getId()).isEmpty()) {
            LocalDate now = LocalDate.now();

            goalRepository.save(Goal.builder().user(user).name("Workout Frequency").targetValue(20).currentValue(18).startDate(now.withDayOfMonth(1)).endDate(now.withDayOfMonth(now.lengthOfMonth())).status(GoalStatus.IN_PROGRESS).isActive(true).build());
            goalRepository.save(Goal.builder().user(user).name("Group Classes").targetValue(15).currentValue(12).startDate(now.withDayOfMonth(1)).endDate(now.withDayOfMonth(now.lengthOfMonth())).status(GoalStatus.IN_PROGRESS).isActive(true).build());
            goalRepository.save(Goal.builder().user(user).name("Recovery Sessions").targetValue(8).currentValue(6).startDate(now.withDayOfMonth(1)).endDate(now.withDayOfMonth(now.lengthOfMonth())).status(GoalStatus.IN_PROGRESS).isActive(true).build());

            log.info("Created demo goals");
        }
    }
}
