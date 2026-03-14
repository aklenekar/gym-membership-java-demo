package com.apexgym.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyContact {
    @Column(name = "emergency_contact_name")
    private String name;
    @Column(name = "emergency_contact_phone")
    private String phone;
    @Column(name = "emergency_contact_relationship")
    private String relationship;
}
