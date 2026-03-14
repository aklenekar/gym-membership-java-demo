package com.apexgym.controller;

import com.apexgym.dto.UserDTO;
import com.apexgym.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final CommonHelper commonHelper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDTO> getUserDetails() {
        String email = commonHelper.getCurrentUserEmail();
        UserDTO userDTO = userService.getUserDetails(email);
        return ResponseEntity.ok(userDTO);
    }
}
