package com.apexgym.service;

import com.apexgym.entity.*;
import com.apexgym.repository.*;
import lombok.RequiredArgsConstructor;
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
    private final ActivityRepository activityRepository;

    @Transactional
    public ClassBooking bookClass(String email, Long classId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GymClass gymClass = gymClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // Check if class is in the future
        if (gymClass.getClassDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book past classes");
        }

        // Check if already booked
        Optional<ClassBooking> existingBooking = classBookingRepository
                .findByUserIdAndGymClassIdAndStatus(user.getId(), classId, BookingStatus.BOOKED);

        if (existingBooking.isPresent()) {
            throw new RuntimeException("Already booked this class");
        }

        // Check capacity
        if (gymClass.getCurrentBookings() >= gymClass.getMaxCapacity()) {
            throw new RuntimeException("Class is full");
        }

        // Create booking
        ClassBooking booking = ClassBooking.builder()
                .user(user)
                .gymClass(gymClass)
                .status(BookingStatus.BOOKED)
                .build();

        classBookingRepository.save(booking);

        // Update class bookings count
        gymClass.setCurrentBookings(gymClass.getCurrentBookings() + 1);
        gymClassRepository.save(gymClass);

        // Create activity
        Activity activity = Activity.builder()
                .user(user)
                .type(ActivityType.CLASS_ATTENDED)
                .title("Booked " + gymClass.getName())
                .icon("📅")
                .build();
        activityRepository.save(activity);

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

        // Cancel booking
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        classBookingRepository.save(booking);

        // Update class bookings count
        GymClass gymClass = booking.getGymClass();
        gymClass.setCurrentBookings(Math.max(0, gymClass.getCurrentBookings() - 1));
        gymClassRepository.save(gymClass);
    }
}