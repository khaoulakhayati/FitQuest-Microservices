package com.fitquest.gamification.repository;

import com.fitquest.gamification.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findByCode(String code);

    List<Badge> findAllByOrderByXpThresholdAsc();
}
