package com.apexgym.payroll.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TrainerPayrollRunRepository extends JpaRepository<TrainerPayrollRun, Long> {
    List<TrainerPayrollRun> findByTrainerId(Long trainerId);
    List<TrainerPayrollRun> findByTrainerEmail(String email);
    List<TrainerPayrollRun> findByStatus(String status);

    @Query("SELECT SUM(p.netPayout) FROM TrainerPayrollRun p WHERE p.status = :status")
    BigDecimal sumNetPayoutByStatus(@Param("status") String status);
}
