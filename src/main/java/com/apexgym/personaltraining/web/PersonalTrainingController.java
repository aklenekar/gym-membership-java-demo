package com.apexgym.personaltraining.web;

import com.apexgym.personaltraining.dto.*;
import com.apexgym.personaltraining.service.PersonalTrainingService;
import com.apexgym.shared.CommonHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personal-training/sessions")
@RequiredArgsConstructor
public class PersonalTrainingController {

    private final PersonalTrainingService personalTrainingService;
    private final CommonHelper commonHelper;

    @PostMapping("/book")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PTSessionDTO> book(@Valid @RequestBody BookSessionRequest request) {
        String email = commonHelper.getCurrentUserEmail();
        return ResponseEntity.ok(personalTrainingService.bookSession(email, request));
    }

    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancel(@PathVariable Long sessionId) {
        String email = commonHelper.getCurrentUserEmail();
        personalTrainingService.cancelSession(email, sessionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{sessionId}/complete")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<PTSessionDTO> complete(@PathVariable Long sessionId) {
        String email = commonHelper.getCurrentUserEmail();
        return ResponseEntity.ok(personalTrainingService.completeSession(email, sessionId));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<PTSessionDTO>> mySessions() {
        String email = commonHelper.getCurrentUserEmail();
        return ResponseEntity.ok(personalTrainingService.getMySessions(email));
    }

    @GetMapping("/trainer")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<PTSessionDTO>> trainerSessions() {
        String email = commonHelper.getCurrentUserEmail();
        return ResponseEntity.ok(personalTrainingService.getTrainerSessions(email));
    }
}