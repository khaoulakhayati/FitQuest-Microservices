package com.fitquest.social.repository;

import com.fitquest.social.entity.Friend;
import com.fitquest.social.entity.FriendStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends MongoRepository<Friend, String> {

    List<Friend> findByUserIdAndStatus(Long userId, FriendStatus status);

    List<Friend> findByUserId(Long userId);

    Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId);

    boolean existsByUserIdAndFriendUserId(Long userId, Long friendUserId);
}
