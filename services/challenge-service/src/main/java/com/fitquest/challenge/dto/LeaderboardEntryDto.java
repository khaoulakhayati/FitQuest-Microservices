package com.fitquest.challenge.dto;

public record LeaderboardEntryDto(
        int rank,
        String id,
        String name,
        int points,
        String type
) {
}
