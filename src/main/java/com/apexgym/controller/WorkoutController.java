package com.apexgym.controller;

import com.apexgym.dto.WorkoutDTO;
import com.apexgym.dto.WorkoutRequest;
import com.apexgym.dto.WorkoutsResponseDTO;
import com.apexgym.service.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final CommonHelper commonHelper;
    private final WorkoutService workoutService;

    @GetMapping
    public ResponseEntity<WorkoutsResponseDTO> getWorkouts(@RequestParam(required = false) String workout,
                                                          @RequestParam(required = false) String day) {
        String email = commonHelper.getCurrentUserEmail();
        WorkoutsResponseDTO workouts = workoutService.getWorkouts(email, workout, day);
        return ResponseEntity.ok(workouts);
    }

    @PostMapping
    public ResponseEntity<WorkoutDTO> submitWorkout(@Valid @RequestBody WorkoutRequest workoutRequest) {
        String email = commonHelper.getCurrentUserEmail();
        WorkoutDTO workout = workoutService.submitWorkoutSession(workoutRequest, email);
        return ResponseEntity.ok(workout);
    }
}
