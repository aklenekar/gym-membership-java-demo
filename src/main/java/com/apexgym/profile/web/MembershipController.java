package com.apexgym.profile.web;

import com.apexgym.admin.dto.PricingResponseDTO;
import com.apexgym.profile.dto.MembershipInfoDTO;
import com.apexgym.profile.dto.UpgradeMembershipRequest;
import com.apexgym.profile.service.MembershipService;
import com.apexgym.shared.CommonHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final CommonHelper commonHelper;
    private final MembershipService membershipService;

    @PutMapping("/upgrade")
    public ResponseEntity<MembershipInfoDTO> upgradePlan(@Valid @RequestBody UpgradeMembershipRequest request) {
        String email = commonHelper.getCurrentUserEmail();
        return ResponseEntity.ok(membershipService.upgradePlan(email, request.plan()));
    }

    @GetMapping("/pricing")
    public ResponseEntity<PricingResponseDTO> getPricing() {
        PricingResponseDTO response = membershipService.getPricing();
        return ResponseEntity.ok(response);
    }
}