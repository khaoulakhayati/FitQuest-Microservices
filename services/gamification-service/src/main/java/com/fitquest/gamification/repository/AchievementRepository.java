package com.fitquest.gamification.repository;

import com.fitquest.gamification.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByUserIdOrderByUnlockedAtDesc(Long userId);

    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);

    Optional<Achievement> findByUserIdAndBadge_Code(Long userId, String badgeCode);

    long countByUserId(Long userId);
}
