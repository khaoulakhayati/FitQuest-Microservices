package com.fitquest.gamification.config;

import com.fitquest.gamification.entity.Achievement;
import com.fitquest.gamification.entity.Badge;
import com.fitquest.gamification.entity.XPHistory;
import com.fitquest.gamification.repository.AchievementRepository;
import com.fitquest.gamification.repository.BadgeRepository;
import com.fitquest.gamification.repository.XPHistoryRepository;
import com.fitquest.gamification.service.AchievementService;
import com.fitquest.gamification.service.XpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final BadgeRepository badgeRepository;
    private final AchievementRepository achievementRepository;
    private final XPHistoryRepository xpHistoryRepository;

    @Bean
    CommandLineRunner seedGamificationData() {
        return args -> {
            if (badgeRepository.count() > 0) {
                return;
            }

            Badge newcomer = badgeRepository.save(Badge.builder()
                    .code(AchievementService.BADGE_NEWCOMER)
                    .name("Newcomer")
                    .description("Joined FitQuest")
                    .iconUrl("/icons/badges/newcomer.png")
                    .category("ONBOARDING")
                    .xpThreshold(0)
                    .build());

            Badge firstWorkout = badgeRepository.save(Badge.builder()
                    .code(AchievementService.BADGE_FIRST_WORKOUT)
                    .name("First Steps")
                    .description("Logged your first workout")
                    .iconUrl("/icons/badges/first-workout.png")
                    .category("WORKOUT")
                    .xpThreshold(0)
                    .build());

            Badge xp100 = badgeRepository.save(Badge.builder()
                    .code(AchievementService.BADGE_XP_100)
                    .name("Century Club")
                    .description("Earned 100 total XP")
                    .iconUrl("/icons/badges/xp-100.png")
                    .category("MILESTONE")
                    .xpThreshold(100)
                    .build());

            Badge xp500 = badgeRepository.save(Badge.builder()
                    .code(AchievementService.BADGE_XP_500)
                    .name("Rising Star")
                    .description("Earned 500 total XP")
                    .iconUrl("/icons/badges/xp-500.png")
                    .category("MILESTONE")
                    .xpThreshold(500)
                    .build());

            Badge xp1000 = badgeRepository.save(Badge.builder()
                    .code(AchievementService.BADGE_XP_1000)
                    .name("Elite Athlete")
                    .description("Earned 1000 total XP")
                    .iconUrl("/icons/badges/xp-1000.png")
                    .category("MILESTONE")
                    .xpThreshold(1000)
                    .build());

            Badge streak7 = badgeRepository.save(Badge.builder()
                    .code("STREAK_7")
                    .name("Week Warrior")
                    .description("Maintained a 7-day activity streak")
                    .iconUrl("/icons/badges/streak-7.png")
                    .category("STREAK")
                    .xpThreshold(250)
                    .build());

            seedDemoUser(1L, newcomer, firstWorkout, xp100, 320);
            seedDemoUser(2L, newcomer, firstWorkout, xp100, 580);
            seedDemoUser(3L, newcomer, null, null, 90);

            log.info("Gamification seed data loaded ({} badges)", badgeRepository.count());
        };
    }

    private void seedDemoUser(Long userId, Badge newcomer, Badge firstWorkout, Badge xp100, int totalXp) {
        xpHistoryRepository.save(XPHistory.builder()
                .userId(userId)
                .amount(totalXp)
                .source(XpService.SOURCE_MANUAL)
                .referenceId("seed")
                .description("Demo seed XP")
                .build());

        achievementRepository.save(Achievement.builder()
                .userId(userId)
                .badge(newcomer)
                .unlockReason("Demo account")
                .build());

        if (firstWorkout != null && totalXp >= 50) {
            achievementRepository.save(Achievement.builder()
                    .userId(userId)
                    .badge(firstWorkout)
                    .unlockReason("Demo workout")
                    .build());
        }

        if (xp100 != null && totalXp >= 100) {
            achievementRepository.save(Achievement.builder()
                    .userId(userId)
                    .badge(xp100)
                    .unlockReason("Demo milestone")
                    .build());
        }
    }
}
