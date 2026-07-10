package com.fitquest.gamification.mapper;

import com.fitquest.gamification.dto.XpHistoryDto;
import com.fitquest.gamification.entity.XPHistory;
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
public class XpHistoryMapperImpl implements XpHistoryMapper {

    @Override
    public XpHistoryDto toDto(XPHistory history) {
        if ( history == null ) {
            return null;
        }

        Long id = null;
        int amount = 0;
        String source = null;
        String referenceId = null;
        String description = null;
        Instant createdAt = null;

        id = history.getId();
        amount = history.getAmount();
        source = history.getSource();
        referenceId = history.getReferenceId();
        description = history.getDescription();
        createdAt = history.getCreatedAt();

        XpHistoryDto xpHistoryDto = new XpHistoryDto( id, amount, source, referenceId, description, createdAt );

        return xpHistoryDto;
    }

    @Override
    public List<XpHistoryDto> toDtoList(List<XPHistory> histories) {
        if ( histories == null ) {
            return null;
        }

        List<XpHistoryDto> list = new ArrayList<XpHistoryDto>( histories.size() );
        for ( XPHistory xPHistory : histories ) {
            list.add( toDto( xPHistory ) );
        }

        return list;
    }
}
