package com.fitquest.gamification.service;

import com.fitquest.gamification.dto.GainXpRequest;
import com.fitquest.gamification.dto.UserXpDto;
import com.fitquest.gamification.dto.XpHistoryDto;
import com.fitquest.gamification.entity.XPHistory;
import com.fitquest.gamification.mapper.XpHistoryMapper;
import com.fitquest.gamification.repository.XPHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class XpService {

    public static final String SOURCE_MANUAL = "MANUAL";
    public static final String SOURCE_WORKOUT = "WORKOUT";
    public static final String SOURCE_REGISTRATION = "REGISTRATION";

    private static final int XP_PER_LEVEL = 100;

    private final XPHistoryRepository xpHistoryRepository;
    private final XpHistoryMapper xpHistoryMapper;
    private final AchievementService achievementService;

    @Value("${fitquest.gamification.xp.welcome:100}")
    private int welcomeXp;

    @Value("${fitquest.gamification.xp.workout-base:25}")
    private int workoutBaseXp;

    @Value("${fitquest.gamification.xp.workout-per-calorie:0.5}")
    private double workoutPerCalorie;

    @Transactional(readOnly = true)
    public UserXpDto getUserXp(Long userId) {
        long totalXp = xpHistoryRepository.sumXpByUserId(userId);
        List<XPHistory> recent = xpHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<XpHistoryDto> history = xpHistoryMapper.toDtoList(
                recent.size() > 10 ? recent.subList(0, 10) : recent);
        return buildUserXpDto(userId, totalXp, history);
    }

    @Transactional
    public UserXpDto gainXp(Long userId, GainXpRequest request) {
        return awardXp(userId, request.amount(), SOURCE_MANUAL, request.referenceId(), request.reason());
    }

    @Transactional
    public UserXpDto awardWorkoutXp(Long userId, Long workoutId, double caloriesBurned) {
        int xp = workoutBaseXp + (int) Math.round(caloriesBurned * workoutPerCalorie);
        xp = Math.max(xp, workoutBaseXp);
        xp = Math.min(xp, 500);
        String ref = workoutId != null ? String.valueOf(workoutId) : null;
        UserXpDto result = awardXp(
                userId,
                xp,
                SOURCE_WORKOUT,
                ref,
                "Workout completed (+" + xp + " XP)");
        achievementService.evaluateAfterWorkout(userId);
        return result;
    }

    @Transactional
    public UserXpDto awardWelcomeXp(Long userId) {
        UserXpDto result = awardXp(
                userId,
                welcomeXp,
                SOURCE_REGISTRATION,
                String.valueOf(userId),
                "Welcome to FitQuest!");
        achievementService.unlockByCode(userId, AchievementService.BADGE_NEWCOMER, "Account created");
        return result;
    }

    @Transactional
    public UserXpDto awardXp(Long userId, int amount, String source, String referenceId, String description) {
        XPHistory entry = XPHistory.builder()
                .userId(userId)
                .amount(amount)
                .source(source)
                .referenceId(referenceId)
                .description(description)
                .build();
        xpHistoryRepository.save(entry);

        long totalXp = xpHistoryRepository.sumXpByUserId(userId);
        achievementService.evaluateXpMilestones(userId, totalXp);

        List<XPHistory> recent = xpHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<XpHistoryDto> history = xpHistoryMapper.toDtoList(
                recent.size() > 10 ? recent.subList(0, 10) : recent);
        return buildUserXpDto(userId, totalXp, history);
    }

    private UserXpDto buildUserXpDto(Long userId, long totalXp, List<XpHistoryDto> history) {
        int level = levelFromXp(totalXp);
        long xpInCurrentLevel = totalXp % XP_PER_LEVEL;
        int xpToNextLevel = XP_PER_LEVEL - (int) xpInCurrentLevel;
        if (xpToNextLevel == XP_PER_LEVEL) {
            xpToNextLevel = 0;
        }
        return new UserXpDto(userId, totalXp, level, xpToNextLevel, history);
    }

    public static int levelFromXp(long totalXp) {
        return (int) (totalXp / XP_PER_LEVEL) + 1;
    }
}
