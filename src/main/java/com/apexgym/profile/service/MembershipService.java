package com.apexgym.profile.service;

import com.apexgym.admin.dto.PricingDTO;
import com.apexgym.admin.dto.PricingResponseDTO;
import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.profile.dto.MembershipInfoDTO;
import com.apexgym.profile.persistence.*;
import com.apexgym.shared.mappers.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final PricingRepository pricingRepository;
    private final AdminMapper adminMapper;

    @Transactional
    public MembershipInfoDTO upgradePlan(String email, String planStr) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MembershipPlan newPlan;
        try {
            newPlan = MembershipPlan.valueOf(planStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid plan: " + planStr);
        }

        Membership membership = membershipRepository.findByUserId(user.getId())
                .orElseGet(() -> Membership.builder()
                        .user(user)
                        .memberSince(LocalDate.now())
                        .build());

        if (membership.getPlan() == newPlan) {
            throw new IllegalArgumentException("Already subscribed to " + newPlan);
        }

        Double price = switch (newPlan) {
            case STARTER -> 29.0;
            case PRO -> 49.0;
            case ELITE -> 79.0;
        };

        membership.setPlan(newPlan);
        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setPrice(price);
        membership.setNextBillingDate(LocalDate.now().plusMonths(1));

        membershipRepository.save(membership);

        return MembershipInfoDTO.builder()
                .plan(membership.getPlan().name())
                .status(membership.getStatus().name())
                .memberSince(membership.getMemberSince())
                .nextBillingDate(membership.getNextBillingDate())
                .price(membership.getPrice())
                .build();
    }

    public PricingResponseDTO getPricing() {
        List<PricingDTO> pricing = pricingRepository.findAll()
                .stream()
                .map(adminMapper::toPricingDTO)
                .collect(Collectors.toList());
        return PricingResponseDTO.builder().pricing(pricing).build();
    }
}