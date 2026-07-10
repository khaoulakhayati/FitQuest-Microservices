package com.fitquest.challenge.controller;

import com.fitquest.challenge.feign.GamificationClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/challenges/gamification")
@RequiredArgsConstructor
@Tag(name = "Challenge Gamification")
public class ChallengeGamificationController {

    private final GamificationClient gamificationClient;

    @GetMapping("/global-leaderboard")
    @Operation(summary = "Fetch global XP leaderboard from gamification-service using OpenFeign")
    public GamificationClient.LeaderboardResponse globalLeaderboard(
            @RequestParam(name = "limit", defaultValue = "20") int limit) {
        return gamificationClient.getGlobalLeaderboard(limit);
    }
}
