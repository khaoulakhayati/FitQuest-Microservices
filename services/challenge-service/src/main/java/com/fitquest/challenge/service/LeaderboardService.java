package com.fitquest.challenge.service;

import com.fitquest.challenge.dto.LeaderboardEntryDto;
import com.fitquest.challenge.entity.Participant;
import com.fitquest.challenge.entity.Team;
import com.fitquest.challenge.exception.NotFoundException;
import com.fitquest.challenge.repository.ChallengeRepository;
import com.fitquest.challenge.repository.ParticipantRepository;
import com.fitquest.challenge.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final ParticipantRepository participantRepository;
    private final TeamRepository teamRepository;
    private final ChallengeRepository challengeRepository;

    public List<LeaderboardEntryDto> participantLeaderboard(String challengeId) {
        ensureChallengeExists(challengeId);
        List<Participant> participants = participantRepository.findByChallengeIdOrderByPointsDesc(challengeId);
        List<LeaderboardEntryDto> entries = new ArrayList<>();
        int rank = 1;
        for (Participant participant : participants) {
            entries.add(new LeaderboardEntryDto(
                    rank++,
                    participant.getId(),
                    participant.getDisplayName(),
                    participant.getPoints(),
                    "PARTICIPANT"
            ));
        }
        return entries;
    }

    public List<LeaderboardEntryDto> teamLeaderboard(String challengeId) {
        ensureChallengeExists(challengeId);
        List<Team> teams = teamRepository.findByChallengeIdOrderByTotalPointsDesc(challengeId);
        List<LeaderboardEntryDto> entries = new ArrayList<>();
        int rank = 1;
        for (Team team : teams) {
            entries.add(new LeaderboardEntryDto(
                    rank++,
                    team.getId(),
                    team.getName(),
                    team.getTotalPoints(),
                    "TEAM"
            ));
        }
        return entries;
    }

    private void ensureChallengeExists(String challengeId) {
        if (!challengeRepository.existsById(challengeId)) {
            throw new NotFoundException("Challenge not found: " + challengeId);
        }
    }
}
