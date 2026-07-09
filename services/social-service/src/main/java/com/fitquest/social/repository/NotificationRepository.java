package com.fitquest.social.repository;

import com.fitquest.social.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(Long userId, Instant since);

    long countByUserIdAndReadFalse(Long userId);
}
