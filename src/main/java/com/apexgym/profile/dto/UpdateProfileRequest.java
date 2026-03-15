package com.apexgym.profile.dto;

import lombok.Builder;

@Builder
public record UpdateProfileRequest(
    PersonalInfoDTO personalInfo,
    AddressDTO address,
    EmergencyContactDTO emergencyContact,
    HealthInfoDTO healthInfo
) {}
