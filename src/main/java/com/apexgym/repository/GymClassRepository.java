package com.apexgym.repository;

import com.apexgym.entity.GymClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GymClassRepository extends JpaRepository<GymClass, Long>, JpaSpecificationExecutor<GymClass> {
    List<GymClass> findByIsActiveTrueAndClassDateAfterOrderByClassDate(LocalDateTime now);

    @Query("SELECT g FROM GymClass g WHERE g.isActive = true AND g.classDate > :now ORDER BY g.classDate")
    List<GymClass> findUpcomingClasses(@Param("now") LocalDateTime now);
}