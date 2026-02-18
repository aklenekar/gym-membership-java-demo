package com.apexgym.repository;

import com.apexgym.entity.BookingStatus;
import com.apexgym.entity.ClassBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {
    List<ClassBooking> findByUserIdAndStatusOrderByGymClass_ClassDateDesc(Long userId, BookingStatus status);

    Optional<ClassBooking> findByUserIdAndGymClassIdAndStatus(Long userId, Long classId, BookingStatus status);

    @Query("SELECT COUNT(cb) FROM ClassBooking cb WHERE cb.user.id = :userId AND cb.status = 'COMPLETED' AND cb.gymClass.classDate >= :startDate")
    Long countCompletedClassesByUserIdAndDateAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    List<ClassBooking> findByUserIdAndStatusAndGymClass_ClassDateAfterOrderByGymClass_ClassDate(Long userId, BookingStatus status, LocalDateTime now);

    List<ClassBooking> findByUserIdAndStatusAndBookedAtAfter(Long userId, BookingStatus status, LocalDateTime date);

    List<ClassBooking> findByUserIdAndBookedAtAfter(Long userId, LocalDateTime date);

    // Admin - NEW queries to add
    @Query("SELECT COUNT(b) FROM ClassBooking b WHERE b.status = 'BOOKED'")
    Long countTotalActiveBookings();

    @Query("SELECT COUNT(b) FROM ClassBooking b WHERE b.status = 'CANCELLED' AND b.bookedAt >= :since")
    Long countCancellationsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(b) FROM ClassBooking b WHERE b.status = 'WAITLISTED'")
    Long countWaitlisted();

    @Query("SELECT COUNT(b) FROM ClassBooking b WHERE b.gymClass.id = :classId AND b.status = com.apexgym.entity.BookingStatus.BOOKED")
    Long countBookingsByClassId(@Param("classId") Long classId);
}