package com.fitquest.challenge.repository;

import com.fitquest.challenge.entity.Participant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends MongoRepository<Participant, String> {

    List<Participant> findByChallengeIdOrderByPointsDesc(String challengeId);

    List<Participant> findByTeamId(String teamId);

    Optional<Participant> findByChallengeIdAndUserId(String challengeId, Long userId);

    boolean existsByChallengeIdAndUserId(String challengeId, Long userId);
}
