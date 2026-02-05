package com.apexgym.controller;

import com.apexgym.dto.TrainerDTO;
import com.apexgym.dto.TrainersResponseDTO;
import com.apexgym.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTrainers() {
        try {
            TrainersResponseDTO trainers = trainerService.getAllTrainers();
            return ResponseEntity.ok(trainers);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to fetch trainers");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllTrainersList() {
        try {
            List<TrainerDTO> trainers = trainerService.getAllTrainersList();
            return ResponseEntity.ok(trainers);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to fetch trainers");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTrainerById(@PathVariable Long id) {
        try {
            TrainerDTO trainer = trainerService.getTrainerById(id);
            return ResponseEntity.ok(trainer);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Trainer not found");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}