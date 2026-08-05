package com.apexgym.shared.initializers.seeders;

import com.apexgym.profile.persistence.Pricing;
import com.apexgym.profile.persistence.PricingFeatures;
import com.apexgym.profile.persistence.PricingRepository;
import com.apexgym.auth.persistence.Role;
import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.booking.persistence.*;
import com.apexgym.profile.persistence.*;
import com.apexgym.profile.persistence.embeddable.Address;
import com.apexgym.profile.persistence.embeddable.EmergencyContact;
import com.apexgym.profile.persistence.embeddable.HealthInfo;
import com.apexgym.tracking.persistence.ClassBooking;
import com.apexgym.tracking.persistence.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ComprehensiveSeeder {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final GymClassRepository gymClassRepository;
    private final ClassBookingRepository classBookingRepository;
    private final GoalRepository goalRepository;
    private final PersonalRecordRepository personalRecordRepository;
    private final AchievementRepository achievementRepository;
    private final PasswordEncoder passwordEncoder;
    private final PricingRepository pricingRepository;

    private final Random random = new Random();

    public void seed() {
        if (userRepository.count() > 10) {
            log.info("Users already exist. Skipping comprehensive initialization.");
            return;
        }

        log.info("Initializing comprehensive user data...");

        List<User> users = createUsers();
        createMemberships(users);
        createWorkoutSessions(users);
        createClassBookings(users);
        createGoals(users);
        createPersonalRecords(users);
        createAchievements(users);
        createPricing();

        log.info("Comprehensive data initialization completed successfully!");
    }

    private List<User> createUsers() {
        String[][] userData = {
                {"Emma", "Wilson", "emma.wilson@email.com"},
                {"Michael", "Brown", "michael.brown@email.com"},
                {"Sophia", "Garcia", "sophia.garcia@email.com"},
                {"James", "Martinez", "james.martinez@email.com"},
                {"Olivia", "Rodriguez", "olivia.rodriguez@email.com"},
                {"William", "Lopez", "william.lopez@email.com"},
                {"Ava", "Hernandez", "ava.hernandez@email.com"},
                {"Benjamin", "Gonzalez", "benjamin.gonzalez@email.com"},
                {"Isabella", "Wilson", "isabella.wilson@email.com"},
                {"Lucas", "Anderson", "lucas.anderson@email.com"}
        };

        List<User> users = new ArrayList<>();
        String encodedPassword = passwordEncoder.encode("password123");

        for (int i = 0; i < userData.length; i++) {
            String[] data = userData[i];
            User user = User.builder()
                    .firstName(data[0])
                    .lastName(data[1])
                    .email(data[2])
                    .password(encodedPassword)
                    .role(Role.USER)
                    .phone(String.format("+1 (555) %03d-%04d", random.nextInt(1000), random.nextInt(10000)))
                    .dateOfBirth(LocalDate.now().minusYears(18 + random.nextInt(30)).minusDays(random.nextInt(365)))
                    .gender(random.nextBoolean() ? "Male" : "Female")
                    .address(Address.builder()
                            .street((100 + random.nextInt(900)) + " Main St")
                            .city("Los Angeles")
                            .state("California")
                            .zipCode(String.format("900%02d", random.nextInt(100)))
                            .country("United States")
                            .build())
                    .emergencyContact(EmergencyContact.builder()
                            .name(data[0] + " Family")
                            .phone(String.format("+1 (555) %03d-%04d", random.nextInt(1000), random.nextInt(10000)))
                            .relationship(i % 2 == 0 ? "Spouse" : "Parent")
                            .build())
                    .healthInfo(HealthInfo.builder()
                            .medicalConditions(i % 4 == 0 ? "Mild asthma" : "None")
                            .fitnessGoals("Build muscle mass and overall strength")
                            .build())
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(180)))
                    .isActive(true)
                    .build();

            users.add(userRepository.save(user));
        }

        return users;
    }

    private void createMemberships(List<User> users) {
        MembershipPlan[] plans = MembershipPlan.values();

        for (User user : users) {
            MembershipPlan plan = plans[random.nextInt(plans.length)];
            boolean isActive = random.nextInt(10) < 9;

            Membership membership = Membership.builder()
                    .user(user)
                    .plan(plan)
                    .status(isActive ? MembershipStatus.ACTIVE : MembershipStatus.EXPIRED)
                    .memberSince(user.getCreatedAt().toLocalDate())
                    .nextBillingDate(isActive ? LocalDate.now().plusDays(random.nextInt(30)) : null)
                    .build();

            membershipRepository.save(membership);
        }
    }

    private void createWorkoutSessions(List<User> users) {
        String[] workoutTypes = {"Upper Body", "Lower Body", "Full Body", "Cardio", "Core", "HIIT"};

        for (User user : users) {
            int workoutCount = 5 + random.nextInt(10);

            for (int i = 0; i < workoutCount; i++) {
                LocalDateTime workoutDate = LocalDateTime.now().minusDays(random.nextInt(60));
                String workoutType = workoutTypes[random.nextInt(workoutTypes.length)];

                WorkoutSession session = WorkoutSession.builder()
                        .user(user)
                        .workoutType(workoutType)
                        .category(GymClassCategory.fromType(workoutType))
                        .startTime(workoutDate)
                        .durationMinutes(45 + random.nextInt(45))
                        .caloriesBurned(300 + random.nextInt(400))
                        .notes("Great session!")
                        .build();

                workoutSessionRepository.save(session);
            }
        }
    }

    private void createClassBookings(List<User> users) {
        List<GymClass> classes = gymClassRepository.findAll();
        if (classes.isEmpty()) return;

        for (User user : users) {
            GymClass targetClass = classes.get(random.nextInt(classes.size()));

            ClassBooking booking = ClassBooking.builder()
                    .user(user)
                    .gymClass(targetClass)
                    .status(BookingStatus.BOOKED)
                    .bookedAt(LocalDateTime.now().minusDays(random.nextInt(10)))
                    .build();

            classBookingRepository.save(booking);
        }
    }

    private void createGoals(List<User> users) {
        for (User user : users) {
            Goal goal = Goal.builder()
                    .user(user)
                    .name("Workout Frequency")
                    .targetValue(20)
                    .currentValue(random.nextInt(15))
                    .startDate(LocalDate.now().minusDays(10))
                    .endDate(LocalDate.now().plusDays(20))
                    .isActive(true)
                    .status(GoalStatus.IN_PROGRESS)
                    .build();

            goalRepository.save(goal);
        }
    }

    private void createPersonalRecords(List<User> users) {
        for (User user : users) {
            PersonalRecord pr = PersonalRecord.builder()
                    .userId(user.getId())
                    .exercise("Bench Press")
                    .value("225 lbs")
                    .icon("🏋️")
                    .achievedAt(LocalDateTime.now().minusDays(random.nextInt(30)))
                    .build();

            personalRecordRepository.save(pr);
        }
    }

    private void createAchievements(List<User> users) {
        for (User user : users) {
            Achievement achievement = Achievement.builder()
                    .userId(user.getId())
                    .name("First Workout")
                    .badge("🎯")
                    .unlockedAt(LocalDateTime.now().minusDays(random.nextInt(45)))
                    .build();

            achievementRepository.save(achievement);
        }
    }

    private void createPricing() {
        Pricing starter = Pricing.builder()
                .name("STARTER")
                .price(29)
                .annualPrice(348)
                .mostFeatured(false)
                .features(List.of(
                        PricingFeatures.builder().mark(true).name("Full gym access").build(),
                        PricingFeatures.builder().mark(true).name("Locker room facilities").build(),
                        PricingFeatures.builder().mark(true).name("Free fitness assessment").build(),
                        PricingFeatures.builder().mark(false).name("Group classes").build(),
                        PricingFeatures.builder().mark(false).name("Personal training").build(),
                        PricingFeatures.builder().mark(false).name("Recovery zone access").build()
                ))
                .build();
        pricingRepository.save(starter);

        Pricing pro = Pricing.builder()
                .name("PRO")
                .price(59)
                .annualPrice(708)
                .mostFeatured(true)
                .features(List.of(
                        PricingFeatures.builder().mark(true).name("24/7 gym access").build(),
                        PricingFeatures.builder().mark(true).name("Unlimited group classes").build(),
                        PricingFeatures.builder().mark(true).name("Recovery zone access").build(),
                        PricingFeatures.builder().mark(true).name("Nutrition consultation").build(),
                        PricingFeatures.builder().mark(true).name("Guest passes (2/month)").build(),
                        PricingFeatures.builder().mark(false).name("Personal training").build()
                ))
                .build();
        pricingRepository.save(pro);

        Pricing elite = Pricing.builder()
                .name("ELITE")
                .price(99)
                .annualPrice(1188)
                .mostFeatured(false)
                .features(List.of(
                        PricingFeatures.builder().mark(true).name("Everything in Pro").build(),
                        PricingFeatures.builder().mark(true).name("4 personal training sessions").build(),
                        PricingFeatures.builder().mark(true).name("Priority class booking").build(),
                        PricingFeatures.builder().mark(true).name("Unlimited guest passes").build(),
                        PricingFeatures.builder().mark(true).name("Exclusive member events").build(),
                        PricingFeatures.builder().mark(true).name("Free merchandise").build()
                ))
                .build();
        pricingRepository.save(elite);
    }
}
