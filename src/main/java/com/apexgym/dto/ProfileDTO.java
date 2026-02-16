package com.apexgym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private PersonalInfoDTO personalInfo;
    private AddressDTO address;
    private EmergencyContactDTO emergencyContact;
    private HealthInfoDTO healthInfo;
    private MembershipInfoDTO membershipInfo;
}
