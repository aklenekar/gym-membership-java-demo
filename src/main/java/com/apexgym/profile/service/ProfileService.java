package com.apexgym.profile.service;

import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.Role;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.booking.persistence.ClassBookingRepository;
import com.apexgym.profile.dto.UserProfile;
import com.apexgym.profile.dto.*;
import com.apexgym.profile.persistence.Membership;
import com.apexgym.profile.persistence.MembershipRepository;
import com.apexgym.profile.persistence.embeddable.Address;
import com.apexgym.profile.persistence.embeddable.EmergencyContact;
import com.apexgym.profile.persistence.embeddable.HealthInfo;
import com.apexgym.shared.mappers.UserMapper;
import com.apexgym.tracking.persistence.ClassBooking;
import com.apexgym.tracking.persistence.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ClassBookingRepository classBookingRepository;
    private final UserMapper userMapper;

    public ProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Membership membership = membershipRepository.findByUserId(user.getId())
                .orElse(null);

        return ProfileDTO.builder()
                .personalInfo(userMapper.toPersonalInfoDTO(user))
                .address(userMapper.toAddressDTO(user.getAddress()))
                .emergencyContact(userMapper.toEmergencyContactDTO(user.getEmergencyContact()))
                .healthInfo(userMapper.toHealthInfoDTO(user.getHealthInfo()))
                .membershipInfo(buildMembershipInfo(user, membership))
                .build();
    }

    @Transactional
    public ProfileDTO updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.personalInfo() != null) {
            updatePersonalInfo(user, request.personalInfo());
        }

        if (request.address() != null) {
            updateAddress(user, request.address());
        }

        if (request.emergencyContact() != null) {
            updateEmergencyContact(user, request.emergencyContact());
        }

        if (request.healthInfo() != null) {
            updateHealthInfo(user, request.healthInfo());
        }

        userRepository.save(user);

        return getProfile(email);
    }

    private MembershipInfoDTO buildMembershipInfo(User user, Membership membership) {
        if (membership == null) {
            return MembershipInfoDTO.builder()
                    .plan("None")
                    .status("Inactive")
                    .build();
        }

        Double amount = switch (membership.getPlan()) {
            case STARTER -> 29.0;
            case PRO -> 49.0;
            case ELITE -> 79.0;
        };

        return MembershipInfoDTO.builder()
                .plan(membership.getPlan().name())
                .status(membership.getStatus().name())
                .nextBillingDate(membership.getNextBillingDate())
                .price(amount)
                .memberSince(user.getCreatedAt().toLocalDate())
                .build();
    }

    private void updatePersonalInfo(User user, PersonalInfoDTO dto) {
        if (dto.firstName() != null) user.setFirstName(dto.firstName());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
        if (dto.phone() != null) user.setPhone(dto.phone());
        if (dto.gender() != null) user.setGender(dto.gender());
    }

    private void updateAddress(User user, AddressDTO dto) {
        if (user.getAddress() == null) user.setAddress(new Address());
        Address addr = user.getAddress();
        if (dto.street() != null) addr.setStreet(dto.street());
        if (dto.city() != null) addr.setCity(dto.city());
        if (dto.state() != null) addr.setState(dto.state());
        if (dto.zipCode() != null) addr.setZipCode(dto.zipCode());
        if (dto.country() != null) addr.setCountry(dto.country());
    }

    private void updateEmergencyContact(User user, EmergencyContactDTO dto) {
        if (user.getEmergencyContact() == null) user.setEmergencyContact(new EmergencyContact());
        EmergencyContact contact = user.getEmergencyContact();
        if (dto.name() != null) contact.setName(dto.name());
        if (dto.phone() != null) contact.setPhone(dto.phone());
        if (dto.relationship() != null) contact.setRelationship(dto.relationship());
    }

    private void updateHealthInfo(User user, HealthInfoDTO dto) {
        if (user.getHealthInfo() == null) user.setHealthInfo(new HealthInfo());
        HealthInfo health = user.getHealthInfo();
        if (dto.medicalConditions() != null) health.setMedicalConditions(dto.medicalConditions());
        if (dto.fitnessGoals() != null) health.setFitnessGoals(dto.fitnessGoals());
    }

    @Transactional
    public ProfileDTO createProfile(CreateProfileRequest req) {
        userRepository.findByEmail(req.email())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("E‑mail already in use");
                });

        User user = User.builder()
                .email(req.email().trim())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .phone(req.phone())
                .gender(req.gender())
                .address(Address.builder()
                        .street(req.street())
                        .city(req.city())
                        .state(req.state())
                        .zipCode(req.zipCode())
                        .country(req.country())
                        .build())
                .emergencyContact(EmergencyContact.builder()
                        .name(req.emergencyContactName())
                        .phone(req.emergencyContactPhone())
                        .relationship(req.emergencyContactRelationship())
                        .build())
                .healthInfo(HealthInfo.builder()
                        .medicalConditions(req.medicalConditions())
                        .fitnessGoals(req.fitnessGoals())
                        .build())
                .role(Role.USER)
                .isActive(true)
                .build();

        if (req.dateOfBirth() != null && !req.dateOfBirth().isBlank()) {
            try {
                user.setDateOfBirth(LocalDate.parse(req.dateOfBirth()));
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("dateOfBirth must be ISO‑8601 (yyyy‑MM‑dd)");
            }
        }

        user.setPassword(passwordEncoder.encode(req.password()));
        User saved = userRepository.save(user);

        return getProfile(saved.getEmail());
    }

    public UserProfile getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Membership membership = membershipRepository.findByUserId(user.getId())
                .orElse(null);

        HealthInfo health = user.getHealthInfo();

        return UserProfile.builder()
                .goals(health != null ? health.getFitnessGoals() : null)
                .level(determineFitnessLevel(user.getId()))
                .availability(determineAvailability(user.getId()))
                .preferences(user.getPreferences())
                .age(calculateAge(user.getDateOfBirth()))
                .membershipPlan(membership != null ? membership.getPlan().name() : "NONE")
                .build();
    }

    private String determineFitnessLevel(Long userId) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        Long workoutCount = workoutSessionRepository.countByUserIdAndStartTimeAfter(userId, threeMonthsAgo);

        if (workoutCount >= 40) return "Advanced";
        if (workoutCount >= 20) return "Intermediate";
        return "Beginner";
    }

    private String determineAvailability(Long userId) {
        List<ClassBooking> recentBookings = classBookingRepository
                .findByUserIdAndBookedAtAfter(userId, LocalDateTime.now().minusMonths(1));

        Map<String, Long> timePreferences = recentBookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> getTimeOfDay(booking.getGymClass().getClassDate()),
                        Collectors.counting()
                ));

        return timePreferences.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Flexible");
    }

    private String getTimeOfDay(LocalDateTime time) {
        int hour = time.getHour();
        if (hour < 12) return "Morning";
        if (hour < 17) return "Afternoon";
        return "Evening";
    }

    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
