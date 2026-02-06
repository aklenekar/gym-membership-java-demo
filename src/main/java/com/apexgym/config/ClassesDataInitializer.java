package com.apexgym.config;

import com.apexgym.entity.FitnessClass;
import com.apexgym.repository.FitnessClassRepository;
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
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class ClassesDataInitializer implements CommandLineRunner {

    private final FitnessClassRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;   // already seeded

        repo.saveAll(List.of(
                FitnessClass.builder()
                        .category("HIIT")
                        .name("HIIT Bootcamp")
                        .instructor("Coach Sarah")
                        .location("Studio A")
                        .startTime(LocalDateTime.now().withHour(18).withMinute(0))
                        .durationMin(60)
                        .capacity(20)
                        .booked(12)
                        .build(),

                FitnessClass.builder()
                        .category("Yoga")
                        .name("Yoga Flow")
                        .instructor("Coach Emma")
                        .location("Studio B")
                        .startTime(LocalDateTime.now().plusDays(1).withHour(7).withMinute(30))
                        .durationMin(60)
                        .capacity(15)
                        .booked(8)
                        .build(),

                FitnessClass.builder()
                        .category("Strength")
                        .name("Strength & Conditioning")
                        .instructor("Coach Tom")
                        .location("Main Floor")
                        .startTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0))
                        .durationMin(75)
                        .capacity(12)
                        .booked(5)
                        .build(),

                FitnessClass.builder()
                        .category("Cardio")
                        .name("Cycling Endurance")
                        .instructor("Coach Mike")
                        .location("Spin Studio")
                        .startTime(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
                                .atTime(17, 30))
                        .durationMin(45)
                        .capacity(20)
                        .booked(18)
                        .build(),

                // … add more as you like …
                FitnessClass.builder()
                        .category("Boxing")
                        .name("Boxing Fundamentals")
                        .instructor("Coach David")
                        .location("Combat Zone")
                        .startTime(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
                                .atTime(10, 0))
                        .durationMin(60)
                        .capacity(15)
                        .booked(10)
                        .build(),

                FitnessClass.builder()
                        .category("Pilates")
                        .name("Core Pilates")
                        .instructor("Coach Jessica")
                        .location("Studio C")
                        .startTime(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                                .atTime(9, 0))
                        .durationMin(50)
                        .capacity(10)
                        .booked(6)
                        .build()
        ));
    }
}
