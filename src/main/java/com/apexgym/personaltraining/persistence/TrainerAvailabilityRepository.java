package com.apexgym.personaltraining.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.DayOfWeek;
import java.util.List;

public interface TrainerAvailabilityRepository extends JpaRepository<TrainerAvailability, Long> {
    List<TrainerAvailability> findByTrainerIdAndIsActiveTrue(Long trainerId);
    List<TrainerAvailability> findByTrainerIdAndDayOfWeekAndIsActiveTrue(Long trainerId, DayOfWeek dayOfWeek);
}