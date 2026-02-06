package com.apexgym.repository;

import com.apexgym.entity.FitnessClass;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FitnessClassRepository extends JpaRepository<FitnessClass, Long>, JpaSpecificationExecutor<FitnessClass> {

    List<FitnessClass> findByCategoryIgnoreCase(String category);

    List<FitnessClass> findByInstructorIgnoreCase(String instructor);

    @Query("SELECT fc FROM FitnessClass fc WHERE DATE(fc.startTime) = :date")
    List<FitnessClass> findByStartDate(@Param("date") LocalDate date);
}