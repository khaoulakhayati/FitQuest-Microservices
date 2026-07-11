package com.fitquest.auth.service;

import com.fitquest.auth.dto.*;
import com.fitquest.auth.entity.Role;
import com.fitquest.auth.entity.User;
import com.fitquest.auth.entity.UserProfile;
import com.fitquest.auth.exception.BadRequestException;
import com.fitquest.auth.exception.UnauthorizedException;
import com.fitquest.auth.keycloak.KeycloakService;
import com.fitquest.auth.keycloak.KeycloakTokenResponse;
import com.fitquest.auth.mapper.UserMapper;
import com.fitquest.auth.messaging.UserEventPublisher;
import com.fitquest.auth.repository.RoleRepository;
import com.fitquest.auth.repository.UserRepository;
import com.fitquest.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserEventPublisher eventPublisher;
    private final KeycloakService keycloakService;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:}")
    private String keycloakJwkSetUri;
    private JwtDecoder keycloakJwtDecoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        String roleName = "coach".equalsIgnoreCase(request.getAccountType()) ? "ROLE_COACH" : "ROLE_USER";
        Role userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Default role not configured"));

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();

        UserProfile profile = UserProfile.builder()
                .user(user)
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername())
                .fitnessGoal("GENERAL_FITNESS")
                .build();
        user.setProfile(profile);

        user = userRepository.save(user);
        keycloakService.createUser(request, user.getId(), roleName);
        log.info("User registered: {}", user.getEmail());
        eventPublisher.publishUserRegistered(user.getId(), user.getEmail(), user.getUsername());

        KeycloakTokenResponse keycloakTokens = keycloakService.isEnabled()
                ? keycloakService.authenticate(request.getEmail(), request.getPassword())
                : null;
        return buildAuthResponse(user, keycloakTokens);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        if (keycloakService.isEnabled()) {
            KeycloakTokenResponse keycloakTokens = keycloakService.authenticate(request.getEmail(), request.getPassword());
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseGet(() -> provisionLocalUserFromKeycloak(request));
            keycloakService.syncFitquestUserId(user.getEmail(), user.getId());
            keycloakTokens = keycloakService.authenticate(request.getEmail(), request.getPassword());
            return buildAuthResponse(user, keycloakTokens);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        validateLocalPassword(request, user);
        return buildAuthResponse(user, null);
    }

    @Transactional
    public AuthResponse loginWithKeycloakToken(KeycloakLoginRequest request) {
        Jwt jwt = decodeKeycloakToken(request.getAccessToken());
        String email = firstNonBlank(jwt.getClaimAsString("email"), jwt.getClaimAsString("preferred_username"));
        if (!StringUtils.hasText(email)) {
            throw new UnauthorizedException("Keycloak token is missing an email");
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> provisionLocalUserFromKeycloakToken(jwt, email));
        keycloakService.syncFitquestUserId(user.getEmail(), user.getId());
        return buildAuthResponse(user, new KeycloakTokenResponse(request.getAccessToken(), null, null, "Bearer"));
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String userId = jwtService.extractUserId(request.getRefreshToken());
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        if (!jwtService.isTokenValid(request.getRefreshToken(), user)) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        return buildAuthResponse(user, null);
    }

    private AuthResponse buildAuthResponse(User user, KeycloakTokenResponse keycloakTokens) {
        String identityProvider = keycloakTokens != null ? "keycloak" : "local";
        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(user, identityProvider))
                .refreshToken(jwtService.generateRefreshToken(user))
                .keycloakAccessToken(keycloakTokens != null ? keycloakTokens.accessToken() : null)
                .keycloakRefreshToken(keycloakTokens != null ? keycloakTokens.refreshToken() : null)
                .identityProvider(identityProvider)
                .tokenType("Bearer")
                .expiresIn(keycloakTokens != null && keycloakTokens.expiresIn() != null ? keycloakTokens.expiresIn() : 86400L)
                .user(userMapper.toDto(user))
                .build();
    }

    private User provisionLocalUserFromKeycloak(LoginRequest request) {
        String email = request.getEmail();
        String username = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
        if (userRepository.existsByUsername(username)) {
            username = username + "-" + System.currentTimeMillis();
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new BadRequestException("Default role not configured"));
        User user = User.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();
        user.setProfile(UserProfile.builder()
                .user(user)
                .displayName(username)
                .fitnessGoal("GENERAL_FITNESS")
                .build());
        user = userRepository.save(user);
        eventPublisher.publishUserRegistered(user.getId(), user.getEmail(), user.getUsername());
        return user;
    }

    private User provisionLocalUserFromKeycloakToken(Jwt jwt, String email) {
        String preferredUsername = firstNonBlank(jwt.getClaimAsString("preferred_username"), email);
        String username = preferredUsername.contains("@")
                ? preferredUsername.substring(0, preferredUsername.indexOf("@"))
                : preferredUsername;
        if (userRepository.existsByUsername(username)) {
            username = username + "-" + System.currentTimeMillis();
        }

        Role role = roleRepository.findByName(primaryRole(jwt))
                .orElseThrow(() -> new BadRequestException("Default role not configured"));
        User user = User.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .roles(Set.of(role))
                .build();
        user.setProfile(UserProfile.builder()
                .user(user)
                .displayName(firstNonBlank(jwt.getClaimAsString("name"), username))
                .fitnessGoal("GENERAL_FITNESS")
                .build());
        user = userRepository.save(user);
        eventPublisher.publishUserRegistered(user.getId(), user.getEmail(), user.getUsername());
        return user;
    }

    private Jwt decodeKeycloakToken(String accessToken) {
        if (!StringUtils.hasText(keycloakJwkSetUri)) {
            throw new UnauthorizedException("Keycloak token validation is not configured");
        }
        try {
            if (keycloakJwtDecoder == null) {
                keycloakJwtDecoder = NimbusJwtDecoder.withJwkSetUri(keycloakJwkSetUri).build();
            }
            return keycloakJwtDecoder.decode(accessToken);
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid Keycloak token");
        }
    }

    @SuppressWarnings("unchecked")
    private String primaryRole(Jwt jwt) {
        Object realmAccess = jwt.getClaims().get("realm_access");
        if (realmAccess instanceof Map<?, ?> realmAccessMap) {
            Object roles = realmAccessMap.get("roles");
            if (roles instanceof Collection<?> roleValues) {
                Set<String> roleNames = roleValues.stream()
                        .map(String::valueOf)
                        .collect(java.util.stream.Collectors.toSet());
                if (roleNames.contains("ROLE_ADMIN")) return "ROLE_ADMIN";
                if (roleNames.contains("ROLE_COACH")) return "ROLE_COACH";
            }
        }
        return "ROLE_USER";
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private void validateLocalPassword(LoginRequest request, User user) {
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
    }
}
