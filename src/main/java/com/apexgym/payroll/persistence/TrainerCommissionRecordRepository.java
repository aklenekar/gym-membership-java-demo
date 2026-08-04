package com.apexgym.payroll.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainerCommissionRecordRepository extends JpaRepository<TrainerCommissionRecord, Long> {
    List<TrainerCommissionRecord> findByTrainerId(Long trainerId);
    List<TrainerCommissionRecord> findByTrainerIdAndSessionDateBetween(Long trainerId, LocalDate start, LocalDate end);
    List<TrainerCommissionRecord> findByStatus(String status);

    @Query("SELECT SUM(c.amount) FROM TrainerCommissionRecord c WHERE c.trainer.id = :trainerId AND c.sessionDate BETWEEN :start AND :end AND c.status <> 'REJECTED'")
    BigDecimal sumCommissionByTrainerAndDateRange(@Param("trainerId") Long trainerId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT SUM(c.amount) FROM TrainerCommissionRecord c WHERE c.status = :status")
    BigDecimal sumCommissionByStatus(@Param("status") String status);
}
