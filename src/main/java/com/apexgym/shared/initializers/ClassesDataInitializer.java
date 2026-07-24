package com.apexgym.shared.initializers;

import com.apexgym.booking.persistence.GymClass;
import com.apexgym.booking.persistence.GymClassCategory;
import com.apexgym.booking.persistence.GymClassRepository;
import com.apexgym.staff.persistence.Trainer;
import com.apexgym.staff.persistence.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class ClassesDataInitializer implements CommandLineRunner {

    private final GymClassRepository repo;
    private final TrainerRepository trainerRepository;

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;   // already seeded
        log.info("Creating Classes information");

        List<GymClass> generatedGymClasses = generateClasses(trainerRepository.findAll(), 60, 30);
        repo.saveAll(generatedGymClasses);
    }

    // Default daily schedule time slots for classes
    private static final List<LocalTime> TIME_SLOTS = List.of(
            LocalTime.of(7, 0),   // Morning slot 1
            LocalTime.of(8, 30),  // Morning slot 2
            LocalTime.of(12, 0),  // Lunch slot
            LocalTime.of(17, 30), // Evening slot 1
            LocalTime.of(18, 45)  // Evening slot 2
    );

    // Configurable parameters per category (Name, Location, Duration, Max Capacity)
    private record CategoryConfig(String name, String location, int duration, int capacity) {}

    private static final Map<GymClassCategory, CategoryConfig> CATEGORY_CONFIGS = Map.of(
            GymClassCategory.HIIT, new CategoryConfig("HIIT Bootcamp", "Studio A", 60, 20),
            GymClassCategory.Yoga, new CategoryConfig("Yoga Flow", "Studio B", 60, 15),
            GymClassCategory.Strength, new CategoryConfig("Strength & Conditioning", "Main Floor", 75, 12),
            GymClassCategory.Cardio, new CategoryConfig("Cycling Endurance", "Spin Studio", 45, 20),
            GymClassCategory.Boxing, new CategoryConfig("Boxing Fundamentals", "Combat Zone", 60, 15),
            GymClassCategory.Pilates, new CategoryConfig("Core Pilates", "Studio C", 50, 10)
    );

    public static List<GymClass> generateClasses(List<Trainer> trainers, int targetTotalClasses, int daysAhead) {
        List<GymClass> classes = new ArrayList<>();
        Random random = new Random();
        LocalDate startDate = LocalDate.now();

        int classesPerTrainer = (int) Math.ceil((double) targetTotalClasses / trainers.size());

        for (Trainer trainer : trainers) {
            String fullInstructorName = trainer.getFirstName() + " " + trainer.getLastName();
            GymClassCategory category = mapSpecialtyToCategory(trainer.getSpecialty());
            CategoryConfig config = CATEGORY_CONFIGS.get(category);

            int trainerClassCount = 0;
            int dayOffset = 0;

            while (trainerClassCount < classesPerTrainer && dayOffset < daysAhead) {
                LocalDate currentDate = startDate.plusDays(dayOffset);

                // Skip Saturday & Sunday
                if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    dayOffset++;
                    continue;
                }

                // Pick a single non-overlapping time slot for this trainer on this day
                // (Assigning 1 class per trainer per working day naturally guarantees no overlaps)
                LocalTime selectedTime = TIME_SLOTS.get((dayOffset + trainer.getFirstName().hashCode()) % TIME_SLOTS.size());
                LocalDateTime classDateTime = currentDate.atTime(selectedTime);

                // Simulate current bookings vs capacity
                int currentBookings = random.nextInt(config.capacity() + 1);

                GymClass newClass = GymClass.builder()
                        .category(category)
                        .name(config.name())
                        .instructorName(fullInstructorName)
                        .location(config.location())
                        .classDate(classDateTime)
                        .durationMinutes(config.duration())
                        .maxCapacity(config.capacity())
                        .currentBookings(currentBookings)
                        .isActive(true)
                        .build();

                classes.add(newClass);
                trainerClassCount++;
                dayOffset++;
            }
        }

        return classes;
    }

    /**
     * Maps trainer specialties directly to Enum categories.
     */
    private static GymClassCategory mapSpecialtyToCategory(String specialty) {
        if (specialty == null) return GymClassCategory.HIIT;

        return switch (specialty) {
            case "Strength & Conditioning", "Powerlifting" -> GymClassCategory.Strength;
            case "HIIT & Conditioning" -> GymClassCategory.HIIT;
            case "Yoga & Mobility" -> GymClassCategory.Yoga;
            case "Boxing & MMA" -> GymClassCategory.Boxing;
            case "Nutrition & Weight Loss" -> GymClassCategory.Pilates;
            default -> GymClassCategory.Cardio;
        };
    }
}
