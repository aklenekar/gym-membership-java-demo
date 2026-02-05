package com.apexgym.repository;

import com.apexgym.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    List<Trainer> findByIsActiveTrueOrderByIsHeadCoachDescYearsExperienceDesc();
    List<Trainer> findByIsHeadCoachTrueAndIsActiveTrue();
    List<Trainer> findByIsHeadCoachFalseAndIsActiveTrue();
}