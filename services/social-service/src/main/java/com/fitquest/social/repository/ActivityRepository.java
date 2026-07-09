package com.fitquest.social.repository;

import com.fitquest.social.entity.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityRepository extends MongoRepository<Activity, String> {

    List<Activity> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Activity> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);
}
