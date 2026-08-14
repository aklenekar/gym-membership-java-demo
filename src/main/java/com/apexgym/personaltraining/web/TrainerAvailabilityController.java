package com.apexgym.personaltraining.web;

import com.apexgym.personaltraining.dto.*;
import com.apexgym.personaltraining.service.TrainerAvailabilityService;
import com.apexgym.shared.CommonHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/personal-training/availability")
@RequiredArgsConstructor
public class TrainerAvailabilityController {

    private final TrainerAvailabilityService availabilityService;
    private final CommonHelper commonHelper;

    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<AvailabilitySlotDTO> addSlot(@Valid @RequestBody CreateAvailabilityRequest request) {
        String email = commonHelper.getCurrentUserEmail();
        return ResponseEntity.ok(availabilityService.addSlot(email, request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<AvailabilitySlotDTO>> getMySlots() {
        String email = commonHelper.getCurrentUserEmail();
        return ResponseEntity.ok(availabilityService.getMySlots(email));
    }

    @DeleteMapping("/{slotId}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long slotId) {
        String email = commonHelper.getCurrentUserEmail();
        availabilityService.deleteSlot(email, slotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{trainerId}/open-slots")
    public ResponseEntity<List<OpenSlotDTO>> getOpenSlots(
            @PathVariable Long trainerId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(availabilityService.getOpenSlots(trainerId, date));
    }
}