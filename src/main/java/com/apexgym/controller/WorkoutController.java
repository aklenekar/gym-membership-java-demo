package com.apexgym.controller;

import com.apexgym.dto.WorkoutsResponseDTO;
import com.apexgym.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final CommonHelper commonHelper;
    private final WorkoutService workoutService;

    @GetMapping
    public ResponseEntity<?> getWorkouts(@RequestParam(required = false) String workout,
                                         @RequestParam(required = false) String day) {
        try {
            String email = commonHelper.getCurrentUserEmail();
            WorkoutsResponseDTO workouts = workoutService.getWorkouts(email, workout, day);
            return ResponseEntity.ok(workouts);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to fetch workouts");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}