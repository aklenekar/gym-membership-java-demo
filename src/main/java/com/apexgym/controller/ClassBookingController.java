package com.apexgym.controller;

import com.apexgym.entity.ClassBooking;
import com.apexgym.service.ClassBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassBookingController {

    private final CommonHelper commonHelper;
    private final ClassBookingService classBookingService;

    @PostMapping("/book/{classId}")
    public ResponseEntity<Map<String, Object>> bookClass(@PathVariable Long classId) {
        String email = commonHelper.getCurrentUserEmail();
        ClassBooking booking = classBookingService.bookClass(email, classId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Class booked successfully");
        response.put("bookingId", booking.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long bookingId) {
        String email = commonHelper.getCurrentUserEmail();
        classBookingService.cancelBooking(email, bookingId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Booking cancelled successfully");

        return ResponseEntity.ok(response);
    }
}
