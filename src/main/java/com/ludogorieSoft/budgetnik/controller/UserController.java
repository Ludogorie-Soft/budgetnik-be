package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.PushTokenRequest;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/exponent-push-token")
    public ResponseEntity<String> updatePushToken(@RequestParam("id")UUID id, @RequestBody PushTokenRequest pushTokenRequest) {
    userService.updateExponentPushToken(id, pushTokenRequest.getToken());
        return ResponseEntity.ok().body("Token updated successfully!");
    }
}
