package com.apexgym.staff.web;

import com.apexgym.shared.CommonHelper;
import com.apexgym.staff.dto.*;
import com.apexgym.staff.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;
    private final CommonHelper commonHelper;

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

    // ============================================================
    // TRAINER LOGIN ENDPOINTS (Require STAFF role)
    // GET /api/trainer/candidates
    // GET /api/trainer/classes
    // ============================================================

    @GetMapping("/candidates")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerCandidatesResponseDTO> getCandidates() {
        String trainerEmail = commonHelper.getCurrentUserEmail();
        TrainerCandidatesResponseDTO response = trainerService.getCandidates(trainerEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/classes")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerClassesResponseDTO> getClasses() {
        String trainerEmail = commonHelper.getCurrentUserEmail();
        TrainerClassesResponseDTO response = trainerService.getClasses(trainerEmail);
        return ResponseEntity.ok(response);
    }
}

