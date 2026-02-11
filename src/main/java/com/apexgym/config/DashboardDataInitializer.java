package com.apexgym.config;

import com.apexgym.entity.*;
import com.apexgym.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class DashboardDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final GymClassRepository gymClassRepository;
    private final ClassBookingRepository classBookingRepository;
    private final ActivityRepository activityRepository;
    private final GoalRepository goalRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing dashboard demo data...");

        Optional<User> userOpt = userRepository.findByEmail("user@apexgym.com");
        if (userOpt.isEmpty()) {
            log.warn("Demo user not found, skipping dashboard data initialization");
            return;
        }

        User user = userOpt.get();

        // Create Membership
        if (membershipRepository.findByUserId(user.getId()).isEmpty()) {
            Membership membership = Membership.builder()
                    .user(user)
                    .plan(MembershipPlan.PRO)
                    .status(MembershipStatus.ACTIVE)
                    .memberSince(LocalDate.of(2025, 1, 15))
                    .nextBillingDate(LocalDate.of(2025, 2, 15))
                    .price(59.0)
                    .autoRenew(true)
                    .build();
            membershipRepository.save(membership);
            log.info("Created membership for demo user");
        }

        // Create Workout Sessions (this month)
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
                        .notes("This is auto generated sessions about " + type)
                        .build();
                workoutSessionRepository.save(session);
            }
            log.info("Created 18 workout sessions");
        }

        // Create Gym Classes
        Optional<GymClass> gymClass = Optional.ofNullable(gymClassRepository.findAll().get(0));
        if (gymClass.isPresent()) {
            // Book first class for user
            ClassBooking booking = ClassBooking.builder()
                    .user(user)
                    .gymClass(gymClass.get())
                    .status(BookingStatus.BOOKED)
                    .build();
            classBookingRepository.save(booking);
            log.info("Booked first class for demo user");
        }

        // Create Activities
        if (activityRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            Activity activity1 = Activity.builder()
                    .user(user)
                    .type(ActivityType.WORKOUT)
                    .title("Completed Upper Body Workout")
                    .icon("💪")
                    .createdAt(now.minusHours(2))
                    .build();
            activityRepository.save(activity1);

            Activity activity2 = Activity.builder()
                    .user(user)
                    .type(ActivityType.CLASS_ATTENDED)
                    .title("Attended HIIT Bootcamp")
                    .icon("🎯")
                    .createdAt(now.minusDays(1))
                    .build();
            activityRepository.save(activity2);

            Activity activity3 = Activity.builder()
                    .user(user)
                    .type(ActivityType.ACHIEVEMENT)
                    .title("Achieved: 10 Workouts This Month")
                    .icon("🏅")
                    .createdAt(now.minusDays(2))
                    .build();
            activityRepository.save(activity3);

            Activity activity4 = Activity.builder()
                    .user(user)
                    .type(ActivityType.CLASS_ATTENDED)
                    .title("Completed Yoga Flow Session")
                    .icon("🧘")
                    .createdAt(now.minusDays(3))
                    .build();
            activityRepository.save(activity4);

            log.info("Created 4 activities");
        }

        // Create Goals
        if (goalRepository.findByUserIdAndIsActiveTrueOrderByStartDateDesc(user.getId()).isEmpty()) {
            LocalDate now = LocalDate.now();

            Goal goal1 = Goal.builder()
                    .user(user)
                    .name("Workout Frequency")
                    .targetValue(20)
                    .currentValue(18)
                    .startDate(now.withDayOfMonth(1))
                    .endDate(now.withDayOfMonth(now.lengthOfMonth()))
                    .status(GoalStatus.IN_PROGRESS)
                    .isActive(true)
                    .build();
            goalRepository.save(goal1);

            Goal goal2 = Goal.builder()
                    .user(user)
                    .name("Group Classes")
                    .targetValue(15)
                    .currentValue(12)
                    .startDate(now.withDayOfMonth(1))
                    .endDate(now.withDayOfMonth(now.lengthOfMonth()))
                    .status(GoalStatus.IN_PROGRESS)
                    .isActive(true)
                    .build();
            goalRepository.save(goal2);

            Goal goal3 = Goal.builder()
                    .user(user)
                    .name("Recovery Sessions")
                    .targetValue(8)
                    .currentValue(6)
                    .startDate(now.withDayOfMonth(1))
                    .endDate(now.withDayOfMonth(now.lengthOfMonth()))
                    .status(GoalStatus.IN_PROGRESS)
                    .isActive(true)
                    .build();
            goalRepository.save(goal3);

            log.info("Created 3 goals");
        }

        log.info("Dashboard demo data initialization completed!");
    }
}
