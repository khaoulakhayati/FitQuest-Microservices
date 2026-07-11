package com.fitquest.auth.controller;

import com.fitquest.auth.dto.*;
import com.fitquest.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT tokens")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/keycloak")
    @Operation(summary = "Create a FitQuest session from a Keycloak access token")
    public AuthResponse keycloakLogin(@Valid @RequestBody KeycloakLoginRequest request) {
        return authService.loginWithKeycloakToken(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }
}
