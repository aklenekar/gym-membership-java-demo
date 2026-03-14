package com.apexgym.controller;

import com.apexgym.dto.CreateProfileRequest;
import com.apexgym.dto.ProfileDTO;
import com.apexgym.dto.UpdateProfileRequest;
import com.apexgym.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final CommonHelper commonHelper;
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile() {
        String email = commonHelper.getCurrentUserEmail();
        ProfileDTO profile = profileService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<ProfileDTO> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String email = commonHelper.getCurrentUserEmail();
        ProfileDTO profile = profileService.updateProfile(email, request);
        return ResponseEntity.ok(profile);
    }

    @PostMapping
    public ResponseEntity<ProfileDTO> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        ProfileDTO profile = profileService.createProfile(request);
        return ResponseEntity.ok(profile);
    }
}
