package com.fitquest.gamification.dto;

import java.util.List;

public record LeaderboardResponse(
        List<LeaderboardEntryDto> entries,
        int limit
) {
}
