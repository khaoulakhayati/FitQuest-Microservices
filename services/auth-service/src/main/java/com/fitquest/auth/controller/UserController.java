package com.fitquest.auth.controller;

import com.fitquest.auth.dto.UpdateUserRequest;
import com.fitquest.auth.dto.UserDto;
import com.fitquest.auth.feign.GamificationClient;
import com.fitquest.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List users for coach group management")
    public List<UserDto> list(@RequestHeader(value = "X-User-Roles", required = false) String roles) {
        return userService.listUsers(roles);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public UserDto getMe(@RequestHeader("X-User-Id") Long userId) {
        return userService.getCurrentUser(userId);
    }

    @GetMapping("/me/xp")
    @Operation(summary = "Get current user XP summary from gamification-service using OpenFeign")
    public GamificationClient.UserXpResponse getMyXp(@RequestHeader("X-User-Id") Long userId) {
        return userService.getCurrentUserXp(userId);
    }

    @PutMapping("/update")
    @Operation(summary = "Update current user profile")
    public UserDto update(@RequestHeader("X-User-Id") Long userId,
                          @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }
}
