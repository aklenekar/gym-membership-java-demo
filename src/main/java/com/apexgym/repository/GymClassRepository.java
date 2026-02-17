package com.apexgym.repository;

import com.apexgym.entity.GymClass;
import com.apexgym.entity.GymClassCategory;
import org.springframework.data.domain.Pageable;
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

    // Admin - NEW queries to add
    @Query("SELECT COUNT(c) FROM GymClass c WHERE c.classDate BETWEEN :start AND :end")
    Long countClassesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /*@Query("""
                SELECT c FROM GymClass c
                WHERE c.isActive = true
                AND (:category IS NULL OR c.category = :category)
                AND (
                    :day IS NULL
                    OR (:day = 'TODAY' AND c.classDate >= :todayStart AND c.classDate < :tomorrowStart)
                    OR (:day = 'TOMORROW' AND c.classDate >= :tomorrowStart AND c.classDate < :weekStart)
                    OR (:day = 'WEEK' AND c.classDate >= :weekStart AND c.classDate < :weekEnd)
                )
                AND (
                    :search IS NULL
                    OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(c.instructorName) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                ORDER BY c.classDate ASC
            """)*/
    @Query("""
                SELECT c FROM GymClass c WHERE c.isActive = true 
                AND (:category IS NULL OR c.category = :category)
                AND (
                    :day IS NULL
                    OR (:day = 'TODAY' AND c.classDate >= :todayStart AND c.classDate < :tomorrowStart)
                    OR (:day = 'TOMORROW' AND c.classDate >= :tomorrowStart AND c.classDate < :weekStart)
                    OR (:day = 'WEEK' AND c.classDate >= :weekStart AND c.classDate < :weekEnd)
                )
            """)
    List<GymClass> findAllWithFilters(
            @Param("category") GymClassCategory category,
            @Param("day") String day,
            @Param("search") String search,
            @Param("todayStart") LocalDateTime todayStart,
            @Param("tomorrowStart") LocalDateTime tomorrowStart,
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd
    );

    // Most popular classes by booking count
    @Query("""
                SELECT c FROM GymClass c
                JOIN ClassBooking b ON b.gymClass.id = c.id
                WHERE b.status = 'COMPLETED'
                GROUP BY c.id
                ORDER BY COUNT(b) DESC
            """)
    List<GymClass> findTopClassesByBookings(Pageable pageable);
}