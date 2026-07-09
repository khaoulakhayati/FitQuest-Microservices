package com.fitquest.social.repository;

import com.fitquest.social.entity.SocialPost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SocialPostRepository extends MongoRepository<SocialPost, String> {
    List<SocialPost> findByGroupIdInOrderByCreatedAtDesc(List<String> groupIds);
}
