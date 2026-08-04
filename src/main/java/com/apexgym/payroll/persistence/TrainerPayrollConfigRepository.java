package com.apexgym.payroll.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerPayrollConfigRepository extends JpaRepository<TrainerPayrollConfig, Long> {
    Optional<TrainerPayrollConfig> findByTrainerId(Long trainerId);
}
