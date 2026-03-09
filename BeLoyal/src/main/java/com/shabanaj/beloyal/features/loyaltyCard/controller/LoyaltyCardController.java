package com.shabanaj.beloyal.features.loyaltyCard.controller;

import com.shabanaj.beloyal.common.Helpers.UserFinder;
import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.loyaltyCard.dto.ChangeLoyaltyCardStatus;
import com.shabanaj.beloyal.features.loyaltyCard.dto.LoyaltyCardDto;
import com.shabanaj.beloyal.features.loyaltyCard.service.LoyaltyCardService;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import com.shabanaj.beloyal.model.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/")
@RequiredArgsConstructor
public class LoyaltyCardController {
    private final LoyaltyCardService loyaltyCardService;
    private final UserFinder userFinder;

    @GetMapping("/customer/me/loyalty-card")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<LoyaltyCardDto> getLoyaltyCard(@AuthenticationPrincipal UserPrincipal userPrincipal){
        User user= userFinder.findByIdOrThrows(userPrincipal.getId());
        LoyaltyCard loyaltyCard= loyaltyCardService.getLoyaltyCardForUser(user);

        // Create response
        LoyaltyCardDto dto= new LoyaltyCardDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setQrToken(loyaltyCard.getQrToken());
        dto.setManualCode(loyaltyCard.getManualCode());

        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/admin/loyalty-card/{userId}/change-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> changeStatus(@PathVariable("userId") Long userId, @RequestBody ChangeLoyaltyCardStatus dto){
        loyaltyCardService.changeStatus(userId, dto.getLoyaltyCardStatus());

        return ResponseEntity.ok(Map.of("message", "Status was successfully changed"));
    }
}
