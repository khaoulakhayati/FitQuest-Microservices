package com.fitquest.challenge.service;

import com.fitquest.challenge.dto.*;
import com.fitquest.challenge.entity.Challenge;
import com.fitquest.challenge.entity.ChallengeStatus;
import com.fitquest.challenge.exception.NotFoundException;
import com.fitquest.challenge.mapper.ChallengeMapper;
import com.fitquest.challenge.messaging.ChallengeEventPublisher;
import com.fitquest.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ParticipantService participantService;
    private final ChallengeEventPublisher eventPublisher;

    public List<ChallengeDto> findAll() {
        return challengeRepository.findAll().stream()
                .map(ChallengeMapper::toDto)
                .toList();
    }

    public List<ChallengeDto> findActive() {
        return challengeRepository.findByStatus(ChallengeStatus.ACTIVE).stream()
                .map(ChallengeMapper::toDto)
                .toList();
    }

    public ChallengeDto getById(String id) {
        return ChallengeMapper.toDto(getChallenge(id));
    }

    public ChallengeDto create(CreateChallengeRequest request) {
        Challenge challenge = Challenge.builder()
                .title(request.title())
                .description(request.description())
                .type(request.type())
                .status(ChallengeStatus.DRAFT)
                .startDate(request.startDate() != null ? request.startDate() : Instant.now())
                .endDate(request.endDate())
                .goalPoints(request.goalPoints())
                .build();
        Challenge saved = challengeRepository.save(challenge);
        eventPublisher.publishChallengeCreated(saved.getId(), saved.getTitle());
        return ChallengeMapper.toDto(saved);
    }

    public ChallengeDto update(String id, UpdateChallengeRequest request) {
        Challenge challenge = getChallenge(id);
        if (request.title() != null) {
            challenge.setTitle(request.title());
        }
        if (request.description() != null) {
            challenge.setDescription(request.description());
        }
        if (request.type() != null) {
            challenge.setType(request.type());
        }
        if (request.status() != null) {
            challenge.setStatus(request.status());
            if (request.status() == ChallengeStatus.COMPLETED) {
                eventPublisher.publishChallengeCompleted(challenge.getId(), challenge.getTitle());
            }
        }
        if (request.startDate() != null) {
            challenge.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            challenge.setEndDate(request.endDate());
        }
        if (request.goalPoints() != null) {
            challenge.setGoalPoints(request.goalPoints());
        }
        return ChallengeMapper.toDto(challengeRepository.save(challenge));
    }

    public void delete(String id) {
        if (!challengeRepository.existsById(id)) {
            throw new NotFoundException("Challenge not found: " + id);
        }
        challengeRepository.deleteById(id);
    }

    public List<ParticipantDto> getParticipants(String challengeId) {
        getChallenge(challengeId);
        return participantService.findByChallenge(challengeId);
    }

    public ParticipantDto join(String challengeId, JoinChallengeRequest request) {
        getChallenge(challengeId);
        return participantService.join(challengeId, request);
    }

    public ParticipantDto addPoints(String challengeId, AddPointsRequest request) {
        getChallenge(challengeId);
        return participantService.addPoints(challengeId, request);
    }

    Challenge getChallenge(String id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Challenge not found: " + id));
    }
}
