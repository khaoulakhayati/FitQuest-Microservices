package com.fitquest.gamification.mapper;

import com.fitquest.gamification.dto.BadgeDto;
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
public class BadgeMapperImpl implements BadgeMapper {

    @Override
    public BadgeDto toDto(Badge badge) {
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

        boolean unlocked = false;

        BadgeDto badgeDto = new BadgeDto( id, code, name, description, iconUrl, category, xpThreshold, createdAt, unlocked );

        return badgeDto;
    }

    @Override
    public List<BadgeDto> toDtoList(List<Badge> badges) {
        if ( badges == null ) {
            return null;
        }

        List<BadgeDto> list = new ArrayList<BadgeDto>( badges.size() );
        for ( Badge badge : badges ) {
            list.add( toDto( badge ) );
        }

        return list;
    }
}
