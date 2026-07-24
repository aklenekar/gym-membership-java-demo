package com.apexgym.shared.initializers.seeders;

import com.apexgym.auth.persistence.Role;
import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void seed() {
        if (userRepository.count() > 0) return;

        log.info("Initializing demo users...");

        String userPassword = passwordEncoder.encode("password123");
        String adminPassword = passwordEncoder.encode("admin123");
        String trainerPassword = passwordEncoder.encode("trainer123");

        List<User> users = List.of(
                buildUser("user@apexgym.com", "Ashwin", "User", Role.USER, userPassword),
                buildUser("admin@apexgym.com", "Admin", "User", Role.ADMIN, adminPassword),
                buildUser("trainer@apexgym.com", "Trainer", "User", Role.TRAINER, trainerPassword),
                buildUser("sarah.mitchell@apexgym.com", "Sarah", "Mitchell", Role.TRAINER, trainerPassword),
                buildUser("mike_rodriguez@apexgym.com", "Mike", "Rodriguez", Role.TRAINER, trainerPassword),
                buildUser("Emma_Chen@apexgym.com", "Emma", "Chen", Role.TRAINER, trainerPassword),
                buildUser("Tom_Jackson@apexgym.com", "Tom", "Jackson", Role.TRAINER, trainerPassword),
                buildUser("Lisa_Parker@apexgym.com", "Lisa", "Parker", Role.TRAINER, trainerPassword),
                buildUser("David_Kim@apexgym.com", "David", "Kim", Role.TRAINER, trainerPassword)
        );

        userRepository.saveAll(users);
        log.info("Demo users created successfully!");
    }

    private User buildUser(String email, String firstName, String lastName, Role role, String password) {
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .password(password)
                .isActive(true)
                .build();
    }
}
