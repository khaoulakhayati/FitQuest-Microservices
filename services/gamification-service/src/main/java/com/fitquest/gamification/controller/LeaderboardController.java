package com.fitquest.gamification.controller;

import com.fitquest.gamification.dto.LeaderboardResponse;
import com.fitquest.gamification.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Global XP leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    @Operation(summary = "Global XP leaderboard")
    public LeaderboardResponse global(@RequestParam(name = "limit", defaultValue = "20") int limit) {
        return leaderboardService.globalLeaderboard(limit);
    }
}
