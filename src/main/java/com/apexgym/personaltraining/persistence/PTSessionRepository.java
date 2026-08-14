package com.apexgym.personaltraining.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface PTSessionRepository extends JpaRepository<PTSession, Long> {

    List<PTSession> findByUserIdOrderByScheduledAtDesc(Long userId);

    List<PTSession> findByTrainerIdOrderByScheduledAtDesc(Long trainerId);

    @Query("SELECT s FROM PTSession s WHERE s.trainer.id = :trainerId AND s.status = 'SCHEDULED' " +
            "AND s.scheduledAt < :end AND FUNCTION('DATEADD', MINUTE, s.durationMinutes, s.scheduledAt) > :start")
    List<PTSession> findOverlapping(@Param("trainerId") Long trainerId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(s) FROM PTSession s WHERE s.user.id = :userId AND s.status IN ('SCHEDULED','COMPLETED') " +
            "AND s.scheduledAt >= :monthStart")
    long countBookedThisMonth(@Param("userId") Long userId, @Param("monthStart") LocalDateTime monthStart);
}