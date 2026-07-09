package com.fitquest.gamification.mapper;

import com.fitquest.gamification.dto.AchievementDto;
import com.fitquest.gamification.entity.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = BadgeMapper.class)
public interface AchievementMapper {

    @Mapping(target = "badge.unlocked", constant = "true")
    AchievementDto toDto(Achievement achievement);

    List<AchievementDto> toDtoList(List<Achievement> achievements);
}
