package com.fitquest.challenge.service;

import com.fitquest.challenge.dto.CreateTeamRequest;
import com.fitquest.challenge.dto.TeamDto;
import com.fitquest.challenge.dto.UpdateTeamRequest;
import com.fitquest.challenge.entity.Team;
import com.fitquest.challenge.exception.NotFoundException;
import com.fitquest.challenge.mapper.ChallengeMapper;
import com.fitquest.challenge.repository.ChallengeRepository;
import com.fitquest.challenge.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final ChallengeRepository challengeRepository;

    public List<TeamDto> findAll(String challengeId) {
        List<Team> teams = challengeId != null
                ? teamRepository.findByChallengeIdOrderByTotalPointsDesc(challengeId)
                : teamRepository.findAll();
        return teams.stream().map(ChallengeMapper::toDto).toList();
    }

    public TeamDto getById(String id) {
        return ChallengeMapper.toDto(getTeamEntity(id));
    }

    public TeamDto create(CreateTeamRequest request) {
        if (!challengeRepository.existsById(request.challengeId())) {
            throw new NotFoundException("Challenge not found: " + request.challengeId());
        }
        Team team = Team.builder()
                .challengeId(request.challengeId())
                .name(request.name())
                .motto(request.motto())
                .totalPoints(0)
                .memberIds(new ArrayList<>())
                .build();
        return ChallengeMapper.toDto(teamRepository.save(team));
    }

    public TeamDto update(String id, UpdateTeamRequest request) {
        Team team = getTeamEntity(id);
        if (request.name() != null) {
            team.setName(request.name());
        }
        if (request.motto() != null) {
            team.setMotto(request.motto());
        }
        return ChallengeMapper.toDto(teamRepository.save(team));
    }

    public void delete(String id) {
        if (!teamRepository.existsById(id)) {
            throw new NotFoundException("Team not found: " + id);
        }
        teamRepository.deleteById(id);
    }

    public void addMember(String teamId, String participantId) {
        Team team = getTeamEntity(teamId);
        if (!team.getMemberIds().contains(participantId)) {
            team.getMemberIds().add(participantId);
            teamRepository.save(team);
        }
    }

    public void addTeamPoints(String teamId, int points) {
        Team team = getTeamEntity(teamId);
        team.setTotalPoints(team.getTotalPoints() + points);
        teamRepository.save(team);
    }

    Team getTeamEntity(String id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Team not found: " + id));
    }
}
