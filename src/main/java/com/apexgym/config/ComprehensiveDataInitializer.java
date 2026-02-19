package com.apexgym.config;

import com.apexgym.entity.*;
import com.apexgym.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Order(4)
@RequiredArgsConstructor
@Slf4j
@Profile("!prod")
public class ComprehensiveDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final GymClassRepository gymClassRepository;
    private final ClassBookingRepository classBookingRepository;
    private final GoalRepository goalRepository;
    private final PersonalRecordRepository personalRecordRepository;
    private final AchievementRepository achievementRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Override
    public void run(String... args) {
        if (userRepository.count() > 5) {
            log.info("✅ Users already exist. Skipping comprehensive initialization.");
            return;
        }

        log.info("🔄 Initializing comprehensive user data...");

        List<User> users = createUsers();
        createMemberships(users);
        createWorkoutSessions(users);
        createClassBookings(users);
        createGoals(users);
        createPersonalRecords(users);
        createAchievements(users);

        log.info("✅ Comprehensive data initialization completed!");
        log.info("📊 Created: {} users with complete profiles", users.size());
    }

    // ============================================================
    // USERS
    // ============================================================

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
                {"Lucas", "Anderson", "lucas.anderson@email.com"},
                {"Mia", "Thomas", "mia.thomas@email.com"},
                {"Alexander", "Taylor", "alexander.taylor@email.com"},
                {"Charlotte", "Moore", "charlotte.moore@email.com"},
                {"Ethan", "Jackson", "ethan.jackson@email.com"},
                {"Amelia", "White", "amelia.white@email.com"}
        };

        List<User> users = new ArrayList<>();

        for (int i = 0; i < userData.length; i++) {
            String[] data = userData[i];
            User user = User.builder()
                    .firstName(data[0])
                    .lastName(data[1])
                    .email(data[2])
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.USER)
                    .phone(generatePhone())
                    .dateOfBirth(generateBirthDate())
                    .gender(random.nextBoolean() ? "Male" : "Female")
                    .street(generateStreet())
                    .city(generateCity())
                    .state("California")
                    .zipCode(String.format("900%02d", random.nextInt(100)))
                    .country("United States")
                    .emergencyContactName(data[0] + " Family")
                    .emergencyContactPhone(generatePhone())
                    .emergencyContactRelationship(i % 3 == 0 ? "Spouse" : i % 3 == 1 ? "Parent" : "Sibling")
                    .medicalConditions(i % 4 == 0 ? "Mild asthma" : "None")
                    .fitnessGoals(generateFitnessGoal())
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(180)))
                    .isActive(Boolean.TRUE)
                    .build();

            users.add(userRepository.save(user));
        }

        log.info("✅ Created {} users", users.size());
        return users;
    }

    // ============================================================
    // MEMBERSHIPS
    // ============================================================

    private void createMemberships(List<User> users) {
        MembershipPlan[] plans = MembershipPlan.values();

        for (User user : users) {
            MembershipPlan plan = plans[random.nextInt(plans.length)];
            boolean isActive = random.nextInt(10) < 9; // 90% active

            Membership membership = Membership.builder()
                    .user(user)
                    .plan(plan)
                    .status(isActive ? MembershipStatus.ACTIVE : MembershipStatus.EXPIRED)
                    .memberSince(user.getCreatedAt().toLocalDate())
                    .nextBillingDate(isActive ? LocalDate.now().plusDays(random.nextInt(30)) : null)
                    .build();

            membershipRepository.save(membership);
        }

        log.info("✅ Created memberships for {} users", users.size());
    }

    // ============================================================
    // WORKOUT SESSIONS
    // ============================================================

    private void createWorkoutSessions(List<User> users) {
        String[] workoutTypes = {"Upper Body", "Lower Body", "Full Body", "Cardio", "Core", "HIIT"};
        int totalWorkouts = 0;

        for (User user : users) {
            int workoutCount = 10 + random.nextInt(30); // 10-40 workouts per user

            for (int i = 0; i < workoutCount; i++) {
                LocalDateTime workoutDate = LocalDateTime.now()
                        .minusDays(random.nextInt(90))
                        .withHour(6 + random.nextInt(15))
                        .withMinute(random.nextInt(60));
                String workoutType = workoutTypes[random.nextInt(workoutTypes.length)];
                WorkoutSession session = WorkoutSession.builder()
                        .user(user)
                        .workoutType(workoutType)
                        .category(GymClassCategory.fromType(workoutType))
                        .startTime(workoutDate)
                        .durationMinutes(30 + random.nextInt(90))
                        .caloriesBurned(200 + random.nextInt(600))
                        .notes(generateWorkoutNote())
                        .build();

                workoutSessionRepository.save(session);
                totalWorkouts++;
            }
        }

        log.info("✅ Created {} workout sessions", totalWorkouts);
    }

    // ============================================================
    // CLASS BOOKINGS
    // ============================================================

    private void createClassBookings(List<User> users) {
        List<GymClass> classes = gymClassRepository.findAll();
        if (classes.isEmpty()) {
            log.warn("⚠️ No gym classes found. Skipping bookings.");
            return;
        }

        int totalBookings = 0;

        for (User user : users) {
            // Past bookings (completed)
            int pastBookings = 5 + random.nextInt(15); // 5-20 past bookings
            Set<Integer> classIds = new HashSet<>();
            for (int i = 0; i < pastBookings; i++) {
                int classId = random.nextInt(classes.size());
                if (classIds.contains(classId)) {
                    classId = random.nextInt(classes.size());
                }
                classIds.add(classId);
                GymClass gymClass = classes.get(random.nextInt(classes.size()));
                LocalDateTime bookingDate = LocalDateTime.now().minusDays(random.nextInt(60));

                ClassBooking booking = ClassBooking.builder()
                        .user(user)
                        .gymClass(gymClass)
                        .status(BookingStatus.COMPLETED)
                        .bookedAt(bookingDate)
                        .build();

                classBookingRepository.save(booking);
                totalBookings++;
            }

            // Upcoming bookings
            int upcomingBookings = 2 + random.nextInt(5); // 2-6 upcoming bookings
            for (int i = 0; i < upcomingBookings; i++) {
                GymClass gymClass = classes.get(random.nextInt(classes.size()));
                LocalDateTime bookingDate = LocalDateTime.now().plusDays(random.nextInt(14));

                ClassBooking booking = ClassBooking.builder()
                        .user(user)
                        .gymClass(gymClass)
                        .status(BookingStatus.BOOKED)
                        .bookedAt(bookingDate)
                        .build();

                classBookingRepository.save(booking);
                totalBookings++;
            }
        }

        log.info("✅ Created {} class bookings (past & upcoming)", totalBookings);
    }

    // ============================================================
    // GOALS
    // ============================================================

    private void createGoals(List<User> users) {
        String[][] goalTemplates = {
                {"Workout Frequency", "20"},
                {"Group Classes", "15"},
                {"Cardio Hours", "10"},
                {"Strength Sessions", "12"},
                {"Weight Loss", "10"},
                {"Muscle Gain", "5"},
                {"Running Distance", "50"},
                {"Flexibility Days", "8"}
        };

        int totalGoals = 0;

        for (User user : users) {
            // Shuffle and pick 5 random goals
            List<String[]> shuffled = new ArrayList<>(Arrays.asList(goalTemplates));
            Collections.shuffle(shuffled);

            for (int i = 0; i < 5; i++) {
                String[] template = shuffled.get(i);
                int targetValue = Integer.parseInt(template[1]);
                int currentValue = random.nextInt(targetValue + 5);

                Goal goal = Goal.builder()
                        .user(user)
                        .name(template[0])
                        .targetValue(targetValue)
                        .currentValue(currentValue)
                        .startDate(LocalDate.now().minusDays(random.nextInt(30)))
                        .endDate(LocalDate.now().plusDays(30 + random.nextInt(30)))
                        .isActive(i < 3) // First 3 goals active
                        .status(GoalStatus.IN_PROGRESS)
                        .build();

                goalRepository.save(goal);
                totalGoals++;
            }
        }

        log.info("✅ Created {} goals", totalGoals);
    }

    // ============================================================
    // PERSONAL RECORDS
    // ============================================================

    private void createPersonalRecords(List<User> users) {
        Object[][] records = {
                {"Bench Press", "225 lbs", "🏋️"},
                {"Squat", "315 lbs", "🦵"},
                {"Deadlift", "405 lbs", "💪"},
                {"5K Run", "22:45", "🏃"},
                {"Pull-ups", "15 reps", "💪"},
                {"Plank", "3:30", "🔥"}
        };

        int totalRecords = 0;

        for (User user : users) {
            int recordCount = 2 + random.nextInt(4); // 2-5 records per user

            for (int i = 0; i < recordCount && i < records.length; i++) {
                Object[] record = records[i];

                PersonalRecord pr = PersonalRecord.builder()
                        .userId(user.getId())
                        .exercise((String) record[0])
                        .value((String) record[1])
                        .icon((String) record[2])
                        .achievedAt(LocalDateTime.now().minusDays(random.nextInt(60)))
                        .build();

                personalRecordRepository.save(pr);
                totalRecords++;
            }
        }

        log.info("✅ Created {} personal records", totalRecords);
    }

    // ============================================================
    // ACHIEVEMENTS
    // ============================================================

    private void createAchievements(List<User> users) {
        String[][] achievements = {
                {"First Workout", "🎯"},
                {"10 Workouts Milestone", "🏆"},
                {"30 Day Streak", "🔥"},
                {"100 Workouts", "💯"},
                {"Strength Master", "💪"},
                {"Cardio King", "🏃"},
                {"Perfect Week", "⭐"},
                {"Early Bird", "🌅"}
        };

        int totalAchievements = 0;

        for (User user : users) {
            int achievementCount = 2 + random.nextInt(4); // 2-5 achievements

            for (int i = 0; i < achievementCount && i < achievements.length; i++) {
                Achievement achievement = Achievement.builder()
                        .userId(user.getId())
                        .name(achievements[i][0])
                        .badge(achievements[i][1])
                        .unlockedAt(LocalDateTime.now().minusDays(random.nextInt(90)))
                        .build();

                achievementRepository.save(achievement);
                totalAchievements++;
            }
        }

        log.info("✅ Created {} achievements", totalAchievements);
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private String generatePhone() {
        return String.format("+1 (555) %03d-%04d", random.nextInt(1000), random.nextInt(10000));
    }

    private LocalDate generateBirthDate() {
        int age = 18 + random.nextInt(45); // 18-63 years old
        return LocalDate.now().minusYears(age).minusDays(random.nextInt(365));
    }

    private String generateStreet() {
        String[] streets = {"Main St", "Oak Ave", "Maple Dr", "Pine Rd", "Cedar Ln", "Elm St"};
        return (100 + random.nextInt(900)) + " " + streets[random.nextInt(streets.length)];
    }

    private String generateCity() {
        String[] cities = {"Los Angeles", "San Francisco", "San Diego", "Sacramento", "Oakland"};
        return cities[random.nextInt(cities.length)];
    }

    private String generateFitnessGoal() {
        String[] goals = {
                "Build muscle mass and increase strength",
                "Lose weight and improve cardiovascular health",
                "Increase flexibility and mobility",
                "Train for marathon",
                "General fitness and wellness",
                "Build lean muscle and tone body",
                "Improve athletic performance",
                "Recovery from injury"
        };
        return goals[random.nextInt(goals.length)];
    }

    private String generateWorkoutNote() {
        if (random.nextInt(3) == 0) return null; // 33% no notes

        String[] notes = {
                "Great session! Felt strong today.",
                "Increased weight by 10 lbs.",
                "Tough workout but pushed through.",
                "Personal record on squats!",
                "Need to focus on form next time.",
                "Excellent cardio session.",
                "Feeling the burn!",
                "New PR on bench press!"
        };
        return notes[random.nextInt(notes.length)];
    }
}