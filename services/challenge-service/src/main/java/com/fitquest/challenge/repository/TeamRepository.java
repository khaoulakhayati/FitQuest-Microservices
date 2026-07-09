package com.fitquest.challenge.repository;

import com.fitquest.challenge.entity.Team;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TeamRepository extends MongoRepository<Team, String> {

    List<Team> findByChallengeIdOrderByTotalPointsDesc(String challengeId);
}
