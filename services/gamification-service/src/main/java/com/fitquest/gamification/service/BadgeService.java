package com.fitquest.gamification.service;

import com.fitquest.gamification.dto.BadgeDto;
import com.fitquest.gamification.entity.Achievement;
import com.fitquest.gamification.entity.Badge;
import com.fitquest.gamification.mapper.BadgeMapper;
import com.fitquest.gamification.repository.AchievementRepository;
import com.fitquest.gamification.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final AchievementRepository achievementRepository;
    private final BadgeMapper badgeMapper;

    @Transactional(readOnly = true)
    public List<BadgeDto> getAllBadges() {
        return badgeMapper.toDtoList(badgeRepository.findAllByOrderByXpThresholdAsc());
    }

    @Transactional(readOnly = true)
    public List<BadgeDto> getBadgesForUser(Long userId) {
        List<Badge> badges = badgeRepository.findAllByOrderByXpThresholdAsc();
        Set<Long> unlockedIds = achievementRepository.findByUserIdOrderByUnlockedAtDesc(userId).stream()
                .map(Achievement::getBadge)
                .map(Badge::getId)
                .collect(Collectors.toSet());

        return badges.stream()
                .map(badge -> badgeMapper.toDto(badge, unlockedIds.contains(badge.getId())))
                .toList();
    }
}
