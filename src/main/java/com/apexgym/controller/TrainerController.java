package com.apexgym.controller;

import com.apexgym.dto.TrainerDTO;
import com.apexgym.dto.TrainersResponseDTO;
import com.apexgym.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/all")
    public ResponseEntity<TrainersResponseDTO> getAllTrainers() {
        TrainersResponseDTO trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TrainerDTO>> getAllTrainersList() {
        List<TrainerDTO> trainers = trainerService.getAllTrainersList();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerDTO> getTrainerById(@PathVariable Long id) {
        TrainerDTO trainer = trainerService.getTrainerById(id);
        return ResponseEntity.ok(trainer);
    }
}
