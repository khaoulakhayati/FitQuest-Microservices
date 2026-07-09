package com.fitquest.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.Instant;
import java.util.List;

@FeignClient(name = "gamification-service")
public interface GamificationClient {

    @GetMapping("/xp")
    UserXpResponse getUserXp(@RequestHeader("X-User-Id") Long userId);

    record UserXpResponse(
            Long userId,
            long totalXp,
            int level,
            int xpToNextLevel,
            List<XpHistoryResponse> recentHistory) {
    }

    record XpHistoryResponse(
            Long id,
            int amount,
            String source,
            String referenceId,
            String description,
            Instant createdAt) {
    }
}
