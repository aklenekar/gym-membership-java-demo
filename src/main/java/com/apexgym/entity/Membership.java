package com.apexgym.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "memberships")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipPlan plan; // STARTER, PRO, ELITE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status; // ACTIVE, INACTIVE, EXPIRED, FROZEN

    @Column(name = "member_since", nullable = false)
    private LocalDate memberSince;

    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    @Column(name = "price")
    private Double price;

    @Column(name = "auto_renew")
    private Boolean autoRenew = true;
}