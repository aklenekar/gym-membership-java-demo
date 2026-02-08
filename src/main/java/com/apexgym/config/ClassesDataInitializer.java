package com.apexgym.config;

import com.apexgym.entity.GymClass;
import com.apexgym.repository.GymClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class ClassesDataInitializer implements CommandLineRunner {

    private final GymClassRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;   // already seeded

        log.info("Creating Classes information");
        repo.saveAll(List.of(
                GymClass.builder()
                        .category("HIIT")
                        .name("HIIT Bootcamp")
                        .instructorName("Coach Sarah")
                        .location("Studio A")
                        .classDate(LocalDateTime.now().withHour(18).withMinute(0))
                        .durationMinutes(60)
                        .maxCapacity(20)
                        .currentBookings(12)
                        .isActive(true)
                        .build(),

                GymClass.builder()
                        .category("Yoga")
                        .name("Yoga Flow")
                        .instructorName("Coach Emma")
                        .location("Studio B")
                        .classDate(LocalDateTime.now().plusDays(1).withHour(7).withMinute(30))
                        .durationMinutes(60)
                        .maxCapacity(15)
                        .currentBookings(8)
                        .isActive(true)
                        .build(),

                GymClass.builder()
                        .category("Strength")
                        .name("Strength & Conditioning")
                        .instructorName("Coach Tom")
                        .location("Main Floor")
                        .classDate(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0))
                        .durationMinutes(75)
                        .maxCapacity(12)
                        .currentBookings(5)
                        .isActive(true)
                        .build(),

                GymClass.builder()
                        .category("Cardio")
                        .name("Cycling Endurance")
                        .instructorName("Coach Mike")
                        .location("Spin Studio")
                        .classDate(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
                                .atTime(17, 30))
                        .durationMinutes(45)
                        .maxCapacity(20)
                        .currentBookings(18)
                        .isActive(true)
                        .build(),

                // … add more as you like …
                GymClass.builder()
                        .category("Boxing")
                        .name("Boxing Fundamentals")
                        .instructorName("Coach David")
                        .location("Combat Zone")
                        .classDate(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
                                .atTime(10, 0))
                        .durationMinutes(60)
                        .maxCapacity(15)
                        .currentBookings(10)
                        .isActive(true)
                        .build(),

                GymClass.builder()
                        .category("Pilates")
                        .name("Core Pilates")
                        .instructorName("Coach Jessica")
                        .location("Studio C")
                        .classDate(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                                .atTime(9, 0))
                        .durationMinutes(50)
                        .maxCapacity(10)
                        .currentBookings(6)
                        .isActive(true)
                        .build()
        ));
    }
}
