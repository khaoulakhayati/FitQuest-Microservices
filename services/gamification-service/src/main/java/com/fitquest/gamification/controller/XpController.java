package com.fitquest.gamification.controller;

import com.fitquest.gamification.dto.GainXpRequest;
import com.fitquest.gamification.dto.UserXpDto;
import com.fitquest.gamification.service.XpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/xp")
@RequiredArgsConstructor
@Tag(name = "XP", description = "Experience points and leveling")
public class XpController {

    private final XpService xpService;

    @GetMapping
    @Operation(summary = "Get XP summary for the current user")
    public UserXpDto getUserXp(@RequestHeader("X-User-Id") Long userId) {
        return xpService.getUserXp(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Award XP to the current user")
    public UserXpDto gainXp(@RequestHeader("X-User-Id") Long userId,
                            @Valid @RequestBody GainXpRequest request) {
        return xpService.gainXp(userId, request);
    }
}
