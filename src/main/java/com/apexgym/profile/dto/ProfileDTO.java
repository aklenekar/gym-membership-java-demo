package com.apexgym.profile.dto;

import lombok.Builder;

@Builder
public record ProfileDTO(
    PersonalInfoDTO personalInfo,
    AddressDTO address,
    EmergencyContactDTO emergencyContact,
    HealthInfoDTO healthInfo,
    MembershipInfoDTO membershipInfo
) {}
