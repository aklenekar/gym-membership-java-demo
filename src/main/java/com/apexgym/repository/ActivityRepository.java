package com.apexgym.repository;

import com.apexgym.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Activity> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
}
