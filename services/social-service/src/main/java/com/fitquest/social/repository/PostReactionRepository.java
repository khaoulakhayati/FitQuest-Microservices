package com.fitquest.social.repository;

import com.fitquest.social.entity.PostReaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PostReactionRepository extends MongoRepository<PostReaction, String> {
    Optional<PostReaction> findByPostIdAndUserId(String postId, Long userId);
}
