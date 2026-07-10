package com.fitquest.challenge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "gamification-service")
public interface GamificationClient {

    @GetMapping("/leaderboard")
    LeaderboardResponse getGlobalLeaderboard(@RequestParam(name = "limit", defaultValue = "20") int limit);

    record LeaderboardResponse(
            List<LeaderboardEntryResponse> entries,
            int limit) {
    }

    record LeaderboardEntryResponse(
            Long userId,
            String username,
            long totalXp,
            int level,
            int rank) {
    }
}
