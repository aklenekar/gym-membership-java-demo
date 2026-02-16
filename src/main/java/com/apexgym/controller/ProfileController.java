package com.apexgym.controller;

import com.apexgym.dto.CreateProfileRequest;
import com.apexgym.dto.ProfileDTO;
import com.apexgym.dto.UpdateProfileRequest;
import com.apexgym.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final CommonHelper commonHelper;
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<?> getProfile() {
        try {
            String email = commonHelper.getCurrentUserEmail();
            ProfileDTO profile = profileService.getProfile(email);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to fetch profile");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            String email = commonHelper.getCurrentUserEmail();
            ProfileDTO profile = profileService.updateProfile(email, request);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update profile");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody CreateProfileRequest request) {
        try {
            ProfileDTO profile = profileService.createProfile(request);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update profile");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
