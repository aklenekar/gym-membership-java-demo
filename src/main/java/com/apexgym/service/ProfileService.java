package com.apexgym.service;

import com.apexgym.dto.*;
import com.apexgym.entity.Membership;
import com.apexgym.entity.Role;
import com.apexgym.entity.User;
import com.apexgym.repository.MembershipRepository;
import com.apexgym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Membership membership = membershipRepository.findByUserId(user.getId())
                .orElse(null);

        return ProfileDTO.builder()
                .personalInfo(buildPersonalInfo(user))
                .address(buildAddress(user))
                .emergencyContact(buildEmergencyContact(user))
                .healthInfo(buildHealthInfo(user))
                .membershipInfo(buildMembershipInfo(user, membership))
                .build();
    }

    @Transactional
    public ProfileDTO updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getPersonalInfo() != null) {
            updatePersonalInfo(user, request.getPersonalInfo());
        }

        if (request.getAddress() != null) {
            updateAddress(user, request.getAddress());
        }

        if (request.getEmergencyContact() != null) {
            updateEmergencyContact(user, request.getEmergencyContact());
        }

        if (request.getHealthInfo() != null) {
            updateHealthInfo(user, request.getHealthInfo());
        }

        userRepository.save(user);

        return getProfile(email);
    }

    private PersonalInfoDTO buildPersonalInfo(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return PersonalInfoDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().format(formatter) : null)
                .gender(user.getGender())
                .build();
    }

    private AddressDTO buildAddress(User user) {
        return AddressDTO.builder()
                .street(user.getStreet())
                .city(user.getCity())
                .state(user.getState())
                .zipCode(user.getZipCode())
                .country(user.getCountry())
                .build();
    }

    private EmergencyContactDTO buildEmergencyContact(User user) {
        return EmergencyContactDTO.builder()
                .name(user.getEmergencyContactName())
                .phone(user.getEmergencyContactPhone())
                .relationship(user.getEmergencyContactRelationship())
                .build();
    }

    private HealthInfoDTO buildHealthInfo(User user) {
        return HealthInfoDTO.builder()
                .medicalConditions(user.getMedicalConditions())
                .fitnessGoals(user.getFitnessGoals())
                .build();
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
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getGender() != null) user.setGender(dto.getGender());
    }

    private void updateAddress(User user, AddressDTO dto) {
        if (dto.getStreet() != null) user.setStreet(dto.getStreet());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        if (dto.getState() != null) user.setState(dto.getState());
        if (dto.getZipCode() != null) user.setZipCode(dto.getZipCode());
        if (dto.getCountry() != null) user.setCountry(dto.getCountry());
    }

    private void updateEmergencyContact(User user, EmergencyContactDTO dto) {
        if (dto.getName() != null) user.setEmergencyContactName(dto.getName());
        if (dto.getPhone() != null) user.setEmergencyContactPhone(dto.getPhone());
        if (dto.getRelationship() != null) user.setEmergencyContactRelationship(dto.getRelationship());
    }

    private void updateHealthInfo(User user, HealthInfoDTO dto) {
        if (dto.getMedicalConditions() != null) user.setMedicalConditions(dto.getMedicalConditions());
        if (dto.getFitnessGoals() != null) user.setFitnessGoals(dto.getFitnessGoals());
    }

    @Transactional
    public ProfileDTO createProfile(CreateProfileRequest req) {
        // -----------------------------------------------------------------
        // 1️⃣  Check for duplicate e‑mail
        // -----------------------------------------------------------------
        userRepository.findByEmail(req.getEmail())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("E‑mail already in use");
                });


        // -----------------------------------------------------------------
        // 2️⃣  Build the User entity + apply defaults
        // -----------------------------------------------------------------
        User.UserBuilder builder = User.builder()
                .email(req.getEmail().trim())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .phone(req.getPhone())
                .gender(req.getGender())
                .street(req.getStreet())
                .city(req.getCity())
                .state(req.getState())
                .zipCode(req.getZipCode())
                .country(req.getCountry())
                .emergencyContactName(req.getEmergencyContactName())
                .emergencyContactPhone(req.getEmergencyContactPhone())
                .emergencyContactRelationship(req.getEmergencyContactRelationship())
                .medicalConditions(req.getMedicalConditions())
                .fitnessGoals(req.getFitnessGoals())
                .role(Role.USER)           // default role for sign‑up
                .isActive(true);

        // -----------------------------------------------------------------
        // 3️⃣  Parse optional dateOfBirth (ISO‑8601) – ignore if malformed
        // -----------------------------------------------------------------
        if (req.getDateOfBirth() != null && !req.getDateOfBirth().isBlank()) {
            try {
                builder.dateOfBirth(LocalDate.parse(req.getDateOfBirth()));
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("dateOfBirth must be ISO‑8601 (yyyy‑MM‑dd)");
            }
        }

        // -----------------------------------------------------------------
        // 4️⃣  Password handling
        // -----------------------------------------------------------------
        String rawPassword = req.getPassword();
        builder.password(passwordEncoder.encode(rawPassword));

        // -----------------------------------------------------------------
        // 5️⃣  Persist & return
        // -----------------------------------------------------------------
        User saved = userRepository.save(builder.build());

        return getProfile(saved.getEmail());
    }
}