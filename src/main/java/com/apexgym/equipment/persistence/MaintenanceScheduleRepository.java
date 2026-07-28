package com.apexgym.equipment.persistence;

import com.apexgym.equipment.entity.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {

    @Query("SELECT s FROM MaintenanceSchedule s WHERE s.lastGeneratedDate IS NULL OR s.lastGeneratedDate <= :targetDate")
    List<MaintenanceSchedule> findDueForGeneration(@Param("targetDate") LocalDate targetDate);
}
