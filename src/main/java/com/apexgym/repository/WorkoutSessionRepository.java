package com.apexgym.repository;

import com.apexgym.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserIdOrderByStartTimeDesc(Long userId);

    @Query("SELECT COUNT(w) FROM WorkoutSession w WHERE w.user.id = :userId AND w.startTime >= :startDate")
    Long countByUserIdAndStartTimeAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(w.durationMinutes), 0) FROM WorkoutSession w WHERE w.user.id = :userId AND w.startTime >= :startDate")
    Long sumDurationByUserIdAndStartTimeAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(w.caloriesBurned), 0) FROM WorkoutSession w WHERE w.user.id = :userId AND w.startTime >= :startDate")
    Long sumCaloriesBurnedByUserIdAndStartTimeAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}