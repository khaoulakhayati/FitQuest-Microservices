package com.fitquest.gamification.service;

import com.fitquest.gamification.dto.AchievementDto;
import com.fitquest.gamification.entity.Achievement;
import com.fitquest.gamification.entity.Badge;
import com.fitquest.gamification.exception.NotFoundException;
import com.fitquest.gamification.mapper.AchievementMapper;
import com.fitquest.gamification.messaging.GamificationEventPublisher;
import com.fitquest.gamification.repository.AchievementRepository;
import com.fitquest.gamification.repository.BadgeRepository;
import com.fitquest.gamification.repository.XPHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    public static final String BADGE_NEWCOMER = "NEWCOMER";
    public static final String BADGE_FIRST_WORKOUT = "FIRST_WORKOUT";
    public static final String BADGE_XP_100 = "XP_100";
    public static final String BADGE_XP_500 = "XP_500";
    public static final String BADGE_XP_1000 = "XP_1000";

    private final AchievementRepository achievementRepository;
    private final BadgeRepository badgeRepository;
    private final XPHistoryRepository xpHistoryRepository;
    private final AchievementMapper achievementMapper;
    private final GamificationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<AchievementDto> getUserAchievements(Long userId) {
        return achievementMapper.toDtoList(
                achievementRepository.findByUserIdOrderByUnlockedAtDesc(userId));
    }

    @Transactional
    public void unlockByCode(Long userId, String badgeCode, String reason) {
        Badge badge = badgeRepository.findByCode(badgeCode)
                .orElseThrow(() -> new NotFoundException("Badge not found: " + badgeCode));
        unlockBadge(userId, badge, reason);
    }

    @Transactional
    public void evaluateAfterWorkout(Long userId) {
        unlockByCode(userId, BADGE_FIRST_WORKOUT, "Logged first workout");
    }

    @Transactional
    public void evaluateXpMilestones(Long userId, long totalXp) {
        if (totalXp >= 100) {
            unlockByCode(userId, BADGE_XP_100, "Reached 100 total XP");
        }
        if (totalXp >= 500) {
            unlockByCode(userId, BADGE_XP_500, "Reached 500 total XP");
        }
        if (totalXp >= 1000) {
            unlockByCode(userId, BADGE_XP_1000, "Reached 1000 total XP");
        }
    }

    private void unlockBadge(Long userId, Badge badge, String reason) {
        if (achievementRepository.existsByUserIdAndBadgeId(userId, badge.getId())) {
            return;
        }
        long totalXp = xpHistoryRepository.sumXpByUserId(userId);
        if (totalXp < badge.getXpThreshold() && !BADGE_NEWCOMER.equals(badge.getCode())
                && !BADGE_FIRST_WORKOUT.equals(badge.getCode())) {
            return;
        }
        Achievement achievement = Achievement.builder()
                .userId(userId)
                .badge(badge)
                .unlockReason(reason)
                .build();
        achievementRepository.save(achievement);
        log.info("Unlocked badge {} for user {}", badge.getCode(), userId);
        eventPublisher.publishAchievementUnlocked(userId, badge.getCode(), reason);
    }
}
