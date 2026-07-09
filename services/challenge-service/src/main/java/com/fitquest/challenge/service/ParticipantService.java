package com.fitquest.challenge.service;

import com.fitquest.challenge.dto.AddPointsRequest;
import com.fitquest.challenge.dto.JoinChallengeRequest;
import com.fitquest.challenge.dto.ParticipantDto;
import com.fitquest.challenge.entity.ChallengeStatus;
import com.fitquest.challenge.entity.Participant;
import com.fitquest.challenge.exception.BadRequestException;
import com.fitquest.challenge.exception.NotFoundException;
import com.fitquest.challenge.mapper.ChallengeMapper;
import com.fitquest.challenge.messaging.ChallengeEventPublisher;
import com.fitquest.challenge.repository.ChallengeRepository;
import com.fitquest.challenge.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ChallengeRepository challengeRepository;
    private final TeamService teamService;
    private final ChallengeEventPublisher eventPublisher;

    public List<ParticipantDto> findByChallenge(String challengeId) {
        return participantRepository.findByChallengeIdOrderByPointsDesc(challengeId).stream()
                .map(ChallengeMapper::toDto)
                .toList();
    }

    public ParticipantDto join(String challengeId, JoinChallengeRequest request) {
        var challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new NotFoundException("Challenge not found: " + challengeId));
        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            throw new BadRequestException("Challenge is not active");
        }
        if (participantRepository.existsByChallengeIdAndUserId(challengeId, request.userId())) {
            throw new BadRequestException("User already joined this challenge");
        }
        if (request.teamId() != null) {
            teamService.getTeamEntity(request.teamId());
        }

        Participant participant = Participant.builder()
                .challengeId(challengeId)
                .userId(request.userId())
                .displayName(request.displayName())
                .teamId(request.teamId())
                .points(0)
                .build();
        Participant saved = participantRepository.save(participant);
        if (request.teamId() != null) {
            teamService.addMember(request.teamId(), saved.getId());
        }
        return ChallengeMapper.toDto(saved);
    }

    public ParticipantDto addPoints(String challengeId, AddPointsRequest request) {
        Participant participant = participantRepository
                .findByChallengeIdAndUserId(challengeId, request.userId())
                .orElseThrow(() -> new NotFoundException("Participant not found in challenge"));
        participant.setPoints(participant.getPoints() + request.points());
        Participant saved = participantRepository.save(participant);
        if (saved.getTeamId() != null) {
            teamService.addTeamPoints(saved.getTeamId(), request.points());
        }
        eventPublisher.publishParticipantScored(
                challengeId, saved.getUserId(), request.points(), saved.getPoints());
        return ChallengeMapper.toDto(saved);
    }
}
