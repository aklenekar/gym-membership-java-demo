package com.apexgym.controller;

import com.apexgym.dto.UserDTO;
import com.apexgym.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final CommonHelper commonHelper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserDetails() {
        try {
            String email = commonHelper.getCurrentUserEmail();
            UserDTO userDTO = userService.getUserDetails(email);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to load user details");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
