package com.fitquest.challenge.mapper;

import com.fitquest.challenge.dto.ChallengeDto;
import com.fitquest.challenge.dto.ParticipantDto;
import com.fitquest.challenge.dto.TeamDto;
import com.fitquest.challenge.entity.Challenge;
import com.fitquest.challenge.entity.Participant;
import com.fitquest.challenge.entity.Team;

public final class ChallengeMapper {

    private ChallengeMapper() {
    }

    public static ChallengeDto toDto(Challenge challenge) {
        return new ChallengeDto(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getType(),
                challenge.getStatus(),
                challenge.getStartDate(),
                challenge.getEndDate(),
                challenge.getGoalPoints(),
                challenge.getCreatedAt(),
                challenge.getUpdatedAt()
        );
    }

    public static TeamDto toDto(Team team) {
        return new TeamDto(
                team.getId(),
                team.getChallengeId(),
                team.getName(),
                team.getMotto(),
                team.getTotalPoints(),
                team.getMemberIds(),
                team.getCreatedAt(),
                team.getUpdatedAt()
        );
    }

    public static ParticipantDto toDto(Participant participant) {
        return new ParticipantDto(
                participant.getId(),
                participant.getChallengeId(),
                participant.getUserId(),
                participant.getTeamId(),
                participant.getDisplayName(),
                participant.getPoints(),
                participant.getJoinedAt(),
                participant.getUpdatedAt()
        );
    }
}
