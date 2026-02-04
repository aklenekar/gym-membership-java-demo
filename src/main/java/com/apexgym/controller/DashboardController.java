package com.apexgym.controller;

import com.apexgym.dto.DashboardResponseDTO;
import com.apexgym.entity.ClassBooking;
import com.apexgym.service.ClassBookingService;
import com.apexgym.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CommonHelper commonHelper;
    private final DashboardService dashboardService;
    private final ClassBookingService classBookingService;

    @GetMapping
    public ResponseEntity<?> getDashboard() {
        try {
            String email = commonHelper.getCurrentUserEmail();
            DashboardResponseDTO dashboard = dashboardService.getDashboardData(email);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to load dashboard");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/book-class/{classId}")
    public ResponseEntity<?> bookClass(@PathVariable Long classId) {
        try {
            String email = commonHelper.getCurrentUserEmail();
            ClassBooking booking = classBookingService.bookClass(email, classId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Class booked successfully");
            response.put("bookingId", booking.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to book class");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/cancel-booking/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            String email = commonHelper.getCurrentUserEmail();
            classBookingService.cancelBooking(email, bookingId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Booking cancelled successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to cancel booking");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }


}