package com.apexgym.shared.initializers.seeders;

import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.staff.persistence.Trainer;
import com.apexgym.staff.persistence.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainerSeeder {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;

    public void seed() {
        if (trainerRepository.count() > 0) return;

        log.info("Initializing trainers data...");

        List<Trainer> trainers = List.of(
                Trainer.builder()
                        .email("sarah.mitchell@apexgym.com")
                        .firstName("Sarah")
                        .lastName("Mitchell")
                        .specialty("Strength & Conditioning")
                        .bio("Former Olympic weightlifting coach with expertise in strength programming and athletic performance. Sarah has trained professional athletes and specializes in functional movement patterns.")
                        .certifications("CSCS, NASM-CPT")
                        .yearsExperience(15)
                        .clientsTrained(500)
                        .rating(4.9)
                        .isHeadCoach(true)
                        .phone("(555) 123-4501")
                        .isActive(true)
                        .build(),

                Trainer.builder()
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
                        .build(),

                Trainer.builder()
                        .email("Emma_Chen@apexgym.com")
                        .firstName("Emma")
                        .lastName("Chen")
                        .specialty("Yoga & Mobility")
                        .yearsExperience(8)
                        .bio("Specializes in vinyasa flow and restorative yoga with emphasis on injury prevention.")
                        .isHeadCoach(false)
                        .isActive(true)
                        .build(),

                Trainer.builder()
                        .email("Tom_Jackson@apexgym.com")
                        .firstName("Tom")
                        .lastName("Jackson")
                        .specialty("Powerlifting")
                        .yearsExperience(10)
                        .bio("Competitive powerlifter focused on maximal strength development and technique.")
                        .isHeadCoach(false)
                        .isActive(true)
                        .build(),

                Trainer.builder()
                        .email("Lisa_Parker@apexgym.com")
                        .firstName("Lisa")
                        .lastName("Parker")
                        .specialty("Nutrition & Weight Loss")
                        .yearsExperience(7)
                        .bio("Registered dietitian offering personalized meal planning and nutritional guidance.")
                        .isHeadCoach(false)
                        .isActive(true)
                        .build(),

                Trainer.builder()
                        .email("David_Kim@apexgym.com")
                        .firstName("David")
                        .lastName("Kim")
                        .specialty("Boxing & MMA")
                        .yearsExperience(9)
                        .bio("Former amateur boxer specializing in combat sports conditioning and technique.")
                        .isHeadCoach(false)
                        .isActive(true)
                        .build()
        );

        // Bind user entities before saving to avoid double-write operations
        trainers.forEach(trainer ->
                userRepository.findByEmail(trainer.getEmail()).ifPresent(trainer::setUser)
        );

        trainerRepository.saveAll(trainers);
        log.info("Trainers data created successfully!");
    }
}