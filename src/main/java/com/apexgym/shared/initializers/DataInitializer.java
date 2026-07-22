package com.apexgym.shared.initializers;

import com.apexgym.auth.persistence.Role;
import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

            List<User> users = new ArrayList<>();

            // Create demo user
            users.add(User.builder()
                    .email("user@apexgym.com")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Ashwin")
                    .lastName("User")
                    .role(Role.USER)
                    .isActive(true)
                    .build());

            // Create admin user
            users.add(User.builder()
                    .email("admin@apexgym.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("User")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build());

            // Create trainer user (generic)
            users.add(User.builder()
                    .email("trainer@apexgym.com")
                    .password(passwordEncoder.encode("trainer123"))
                    .firstName("Trainer")
                    .lastName("User")
                    .role(Role.TRAINER)
                    .isActive(true)
                    .build());

            // Create trainer users for each trainer
            users.add(User.builder()
                    .email("sarah.mitchell@apexgym.com")
                    .password(passwordEncoder.encode("trainer123"))
                    .firstName("Sarah")
                    .lastName("Mitchell")
                    .role(Role.TRAINER)
                    .isActive(true)
                    .build());

            users.add(User.builder()
                    .email("mike_rodriguez@apexgym.com")
                    .password(passwordEncoder.encode("trainer123"))
                    .firstName("Mike")
                    .lastName("Rodriguez")
                    .role(Role.TRAINER)
                    .isActive(true)
                    .build());

            users.add(User.builder()
                    .email("Emma_Chen@apexgym.com")
                    .password(passwordEncoder.encode("trainer123"))
                    .firstName("Emma")
                    .lastName("Chen")
                    .role(Role.TRAINER)
                    .isActive(true)
                    .build());

            users.add(User.builder()
                    .email("Tom_Jackson@apexgym.com")
                    .password(passwordEncoder.encode("trainer123"))
                    .firstName("Tom")
                    .lastName("Jackson")
                    .role(Role.TRAINER)
                    .isActive(true)
                    .build());

            users.add(User.builder()
                    .email("Lisa_Parker@apexgym.com")
                    .password(passwordEncoder.encode("trainer123"))
                    .firstName("Lisa")
                    .lastName("Parker")
                    .role(Role.TRAINER)
                    .isActive(true)
                    .build());

            users.add(User.builder()
                    .email("David_Kim@apexgym.com")
                    .password(passwordEncoder.encode("trainer123"))
                    .firstName("David")
                    .lastName("Kim")
                    .role(Role.TRAINER)
                    .isActive(true)
                    .build());

            userRepository.saveAll(users);

            log.info("Demo users created successfully!");
        }
    }
}