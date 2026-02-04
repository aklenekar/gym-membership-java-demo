package com.apexgym.config;

import com.apexgym.entity.Role;
import com.apexgym.entity.User;
import com.apexgym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create demo users if database is empty
        if (userRepository.count() == 0) {
            log.info("Initializing demo users...");

            // Create demo user
            User demoUser = User.builder()
                    .email("user@apexgym.com")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Ashwin")
                    .lastName("User")
                    .role(Role.USER)
                    .isActive(true)
                    .build();

            // Create admin user
            User adminUser = User.builder()
                    .email("admin@apexgym.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("User")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            // Create trainer user
            User trainerUser = User.builder()
                    .email("trainer@apexgym.com")
                    .password(passwordEncoder.encode("trainer123"))
                    .firstName("Trainer")
                    .lastName("User")
                    .role(Role.TRAINER)
                    .isActive(true)
                    .build();

            userRepository.save(demoUser);
            userRepository.save(adminUser);
            userRepository.save(trainerUser);

            log.info("Demo users created successfully!");
            log.info("User: user@apexgym.com / password123");
            log.info("Admin: admin@apexgym.com / admin123");
            log.info("Trainer: trainer@apexgym.com / trainer123");
        }
    }
}