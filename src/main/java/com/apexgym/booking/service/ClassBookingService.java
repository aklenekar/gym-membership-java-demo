package com.apexgym.booking.service;

import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.booking.event.ClassBookedEvent;
import com.apexgym.booking.persistence.*;
import com.apexgym.tracking.persistence.ClassBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassBookingService {

    private final ClassBookingRepository classBookingRepository;
    private final GymClassRepository gymClassRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ClassBooking bookClass(String email, Long classId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GymClass gymClass = gymClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        if (gymClass.getClassDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book past classes");
        }

        Optional<ClassBooking> existingBooking = classBookingRepository
                .findByUserIdAndGymClassIdAndStatus(user.getId(), classId, BookingStatus.BOOKED);

        if (existingBooking.isPresent()) {
            throw new RuntimeException("Already booked this class");
        }

        if (gymClass.getCurrentBookings() >= gymClass.getMaxCapacity()) {
            throw new RuntimeException("Class is full");
        }

        ClassBooking booking = ClassBooking.builder()
                .user(user)
                .gymClass(gymClass)
                .status(BookingStatus.BOOKED)
                .build();

        classBookingRepository.save(booking);

        gymClass.setCurrentBookings(gymClass.getCurrentBookings() + 1);
        gymClassRepository.save(gymClass);

        eventPublisher.publishEvent(new ClassBookedEvent(this, user, gymClass.getName()));

        return booking;
    }

    @Transactional
    public void cancelBooking(String email, Long bookingId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClassBooking booking = classBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new RuntimeException("Booking cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        classBookingRepository.save(booking);

        GymClass gymClass = booking.getGymClass();
        gymClass.setCurrentBookings(Math.max(0, gymClass.getCurrentBookings() - 1));
        gymClassRepository.save(gymClass);
    }
}
