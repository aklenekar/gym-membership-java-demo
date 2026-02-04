package com.apexgym.repository;

import com.apexgym.entity.Goal;
import com.apexgym.entity.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserIdAndIsActiveTrueOrderByStartDateDesc(Long userId);
    List<Goal> findByUserIdAndStatusOrderByStartDateDesc(Long userId, GoalStatus status);
}