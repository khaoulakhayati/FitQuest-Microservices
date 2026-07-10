package com.fitquest.gamification.mapper;

import com.fitquest.gamification.dto.AchievementDto;
import com.fitquest.gamification.dto.BadgeDto;
import com.fitquest.gamification.entity.Achievement;
import com.fitquest.gamification.entity.Badge;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-12T11:57:32+0100",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class AchievementMapperImpl implements AchievementMapper {

    @Override
    public AchievementDto toDto(Achievement achievement) {
        if ( achievement == null ) {
            return null;
        }

        Long id = null;
        Long userId = null;
        BadgeDto badge = null;
        String unlockReason = null;
        Instant unlockedAt = null;

        id = achievement.getId();
        userId = achievement.getUserId();
        badge = badgeToBadgeDto( achievement.getBadge() );
        unlockReason = achievement.getUnlockReason();
        unlockedAt = achievement.getUnlockedAt();

        AchievementDto achievementDto = new AchievementDto( id, userId, badge, unlockReason, unlockedAt );

        return achievementDto;
    }

    @Override
    public List<AchievementDto> toDtoList(List<Achievement> achievements) {
        if ( achievements == null ) {
            return null;
        }

        List<AchievementDto> list = new ArrayList<AchievementDto>( achievements.size() );
        for ( Achievement achievement : achievements ) {
            list.add( toDto( achievement ) );
        }

        return list;
    }

    protected BadgeDto badgeToBadgeDto(Badge badge) {
        if ( badge == null ) {
            return null;
        }

        Long id = null;
        String code = null;
        String name = null;
        String description = null;
        String iconUrl = null;
        String category = null;
        int xpThreshold = 0;
        Instant createdAt = null;

        id = badge.getId();
        code = badge.getCode();
        name = badge.getName();
        description = badge.getDescription();
        iconUrl = badge.getIconUrl();
        category = badge.getCategory();
        xpThreshold = badge.getXpThreshold();
        createdAt = badge.getCreatedAt();

        boolean unlocked = true;

        BadgeDto badgeDto = new BadgeDto( id, code, name, description, iconUrl, category, xpThreshold, createdAt, unlocked );

        return badgeDto;
    }
}
