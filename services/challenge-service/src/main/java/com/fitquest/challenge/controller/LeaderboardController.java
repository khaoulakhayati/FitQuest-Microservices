package com.fitquest.challenge.controller;

import com.fitquest.challenge.dto.LeaderboardEntryDto;
import com.fitquest.challenge.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    @Operation(summary = "Participant leaderboard for a challenge")
    public List<LeaderboardEntryDto> participants(
            @RequestParam String challengeId) {
        return leaderboardService.participantLeaderboard(challengeId);
    }

    @GetMapping("/teams")
    @Operation(summary = "Team leaderboard for a challenge")
    public List<LeaderboardEntryDto> teams(@RequestParam String challengeId) {
        return leaderboardService.teamLeaderboard(challengeId);
    }
}
