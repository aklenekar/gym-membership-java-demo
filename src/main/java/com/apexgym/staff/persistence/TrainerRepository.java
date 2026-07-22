package com.apexgym.staff.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    List<Trainer> findByIsActiveTrueOrderByIsHeadCoachDescYearsExperienceDesc();
    List<Trainer> findByIsHeadCoachTrueAndIsActiveTrue();
    List<Trainer> findByIsHeadCoachFalseAndIsActiveTrue();
    Long countByIsActiveTrue();
    List<Trainer> findTop3ByIsActiveTrueOrderByRatingDesc();

    @Query("SELECT t FROM Trainer t WHERE t.user.email = :email")
    Optional<Trainer> findByUserEmail(@Param("email") String email);
}


