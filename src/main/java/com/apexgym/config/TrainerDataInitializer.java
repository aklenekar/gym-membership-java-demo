package com.apexgym.config;

import com.apexgym.entity.Role;
import com.apexgym.entity.Trainer;
import com.apexgym.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class TrainerDataInitializer implements CommandLineRunner {

    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (trainerRepository.count() == 0) {
            log.info("Initializing trainers data");

            List<Trainer> trainers = new ArrayList<>();

            trainers.add(Trainer.builder()
                    .firstName("Sarah")
                    .lastName("Mitchell")
                    .specialty("Strength & Conditioning")
                    .bio("Former Olympic weightlifting coach with expertise in strength programming and athletic performance. Sarah has trained professional athletes and specializes in functional movement patterns.")
                    .certifications("CSCS, NASM-CPT")
                    .yearsExperience(15)
                    .clientsTrained(500)
                    .rating(4.9)
                    .isHeadCoach(true)
                    .email("sarah.mitchell@apexgym.com")
                    .phone("(555) 123-4501")
                    .isActive(true)
                    .build());

            trainers.add(Trainer.builder()
                    .email("mike_rodriguez@apexgym.com")
                    .firstName("Mike")
                    .lastName("Rodriguez")
                    .specialty("HIIT & Conditioning")
                    .yearsExperience(12)
                    .bio("High-intensity training specialist focused on metabolic conditioning and body transformation. Mike's bootcamp classes are legendary for delivering results.")
                    .clientsTrained(400)
                    .rating(5.0)
                    .isHeadCoach(true)
                    .isActive(true)
                    .build());

            trainers.add(Trainer.builder()
                    .email("Emma_Chen@apexgym.com")
                    .firstName("Emma")
                    .lastName("Chen")
                    .specialty("Yoga & Mobility")
                    .yearsExperience(8)
                    .bio("Specializes in vinyasa flow and restorative yoga with emphasis on injury prevention.")
                    .isHeadCoach(false)
                    .isActive(true)
                    .build());

            trainers.add(Trainer.builder()
                    .email("Tom_Jackson@apexgym.com")
                    .firstName("Tom")
                    .lastName("Jackson")
                    .specialty("Powerlifting")
                    .yearsExperience(10)
                    .bio("Competitive powerlifter focused on maximal strength development and technique.")
                    .isHeadCoach(false)
                    .isActive(true)
                    .build());

            trainers.add(Trainer.builder()
                    .email("Lisa_Parker@apexgym.com")
                    .firstName("Lisa")
                    .lastName("Parker")
                    .specialty("Nutrition & Weight Loss")
                    .yearsExperience(7)
                    .bio("Registered dietitian offering personalized meal planning and nutritional guidance.")
                    .isHeadCoach(false)
                    .isActive(true)
                    .build());

            trainers.add(Trainer.builder()
                    .email("David_Kim@apexgym.com")
                    .firstName("David")
                    .lastName("Kim")
                    .specialty("Boxing & MMA")
                    .yearsExperience(9)
                    .bio("Former amateur boxer specializing in combat sports conditioning and technique.")
                    .isHeadCoach(false)
                    .isActive(true)
                    .build());

            trainerRepository.saveAll(trainers);
            log.info("Trainers data created successfully");
        }
    }
}
