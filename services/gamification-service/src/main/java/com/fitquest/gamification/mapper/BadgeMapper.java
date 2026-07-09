package com.fitquest.gamification.mapper;

import com.fitquest.gamification.dto.BadgeDto;
import com.fitquest.gamification.entity.Badge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BadgeMapper {

    @Mapping(target = "unlocked", constant = "false")
    BadgeDto toDto(Badge badge);

    List<BadgeDto> toDtoList(List<Badge> badges);

    default BadgeDto toDto(Badge badge, boolean unlocked) {
        return new BadgeDto(
                badge.getId(),
                badge.getCode(),
                badge.getName(),
                badge.getDescription(),
                badge.getIconUrl(),
                badge.getCategory(),
                badge.getXpThreshold(),
                badge.getCreatedAt(),
                unlocked
        );
    }
}
