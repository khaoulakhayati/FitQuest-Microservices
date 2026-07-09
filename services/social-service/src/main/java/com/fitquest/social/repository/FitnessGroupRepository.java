package com.fitquest.social.repository;

import com.fitquest.social.entity.FitnessGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FitnessGroupRepository extends MongoRepository<FitnessGroup, String> {
    List<FitnessGroup> findByCoachIdOrderByCreatedAtDesc(Long coachId);
}
