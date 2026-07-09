package com.fitquest.challenge.repository;

import com.fitquest.challenge.entity.Challenge;
import com.fitquest.challenge.entity.ChallengeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChallengeRepository extends MongoRepository<Challenge, String> {

    List<Challenge> findByStatus(ChallengeStatus status);
}
