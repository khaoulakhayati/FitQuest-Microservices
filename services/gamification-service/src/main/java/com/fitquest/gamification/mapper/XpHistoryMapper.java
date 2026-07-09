package com.fitquest.gamification.mapper;

import com.fitquest.gamification.dto.XpHistoryDto;
import com.fitquest.gamification.entity.XPHistory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface XpHistoryMapper {

    XpHistoryDto toDto(XPHistory history);

    List<XpHistoryDto> toDtoList(List<XPHistory> histories);
}
