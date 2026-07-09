package com.fitquest.social.repository;

import com.fitquest.social.entity.GroupMember;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends MongoRepository<GroupMember, String> {
    boolean existsByGroupIdAndUserId(String groupId, Long userId);
    List<GroupMember> findByGroupId(String groupId);
    List<GroupMember> findByUserId(Long userId);
    Optional<GroupMember> findByGroupIdAndUserId(String groupId, Long userId);
}
