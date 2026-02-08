package com.apexgym.controller;

import com.apexgym.dto.GymClassDTO;
import com.apexgym.service.FitnessClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class FitnessClassController {

    private final FitnessClassService service;
    private final CommonHelper commonHelper;

    /**
     * -------------------------------------------------------------
     * GET  /api/classes
     * Optional query params: category, instructor, day
     * -------------------------------------------------------------
     */
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String instructor,
            @RequestParam(required = false) String day) {
        try {
            String email = commonHelper.getCurrentUserEmail();
            List<GymClassDTO> classes = service.findByFilters(category, instructor, day, email);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to load classes");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * -------------------------------------------------------------
     * GET  /api/classes/{id}
     * -------------------------------------------------------------
     */
    @GetMapping("/{id}")
    public ResponseEntity<GymClassDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * -------------------------------------------------------------
     * POST /api/classes/{id}/book   → book a spot
     * -------------------------------------------------------------
     */
    @PostMapping("/{id}/book")
    public ResponseEntity<?> book(@PathVariable Long id) {
        try {
            GymClassDTO booked = service.bookClass(id);
            return ResponseEntity.ok(booked);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to book class");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * -------------------------------------------------------------
     * DELETE /api/classes/{id}/book   → cancel a spot
     * -------------------------------------------------------------
     */
    @DeleteMapping("/{id}/book")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            GymClassDTO cancelled = service.cancelBooking(id);
            return ResponseEntity.ok(cancelled);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to cancel class");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
