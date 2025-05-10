package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.service.impl.CommunityLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityLinkController {
    private final CommunityLinkService communityLinkService;

    @GetMapping("/viber")
    public ResponseEntity<String> getViberLink() {
        String response = communityLinkService.getViberLink();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/telegram")
    public ResponseEntity<String> getTelegramLink() {
        String response = communityLinkService.getTelegramLink();
        return ResponseEntity.ok(response);
    }
}
