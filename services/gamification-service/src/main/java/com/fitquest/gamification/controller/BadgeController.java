package com.fitquest.gamification.controller;

import com.fitquest.gamification.dto.AchievementDto;
import com.fitquest.gamification.dto.BadgeDto;
import com.fitquest.gamification.service.AchievementService;
import com.fitquest.gamification.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
@Tag(name = "Badges", description = "Badge catalog and user achievements")
public class BadgeController {

    private final BadgeService badgeService;
    private final AchievementService achievementService;

    @GetMapping
    @Operation(summary = "List all badge definitions")
    public List<BadgeDto> getAllBadges() {
        return badgeService.getAllBadges();
    }

    @GetMapping("/me")
    @Operation(summary = "List badges with unlock status for the current user")
    public List<BadgeDto> getMyBadges(@RequestHeader("X-User-Id") Long userId) {
        return badgeService.getBadgesForUser(userId);
    }

    @GetMapping("/achievements")
    @Operation(summary = "List achievements unlocked by the current user")
    public List<AchievementDto> getMyAchievements(@RequestHeader("X-User-Id") Long userId) {
        return achievementService.getUserAchievements(userId);
    }
}
