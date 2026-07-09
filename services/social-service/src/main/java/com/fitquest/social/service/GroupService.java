package com.fitquest.social.service;

import com.fitquest.social.dto.AddGroupMemberRequest;
import com.fitquest.social.dto.CreateGroupRequest;
import com.fitquest.social.dto.FitnessGroupDto;
import com.fitquest.social.entity.ActivityType;
import com.fitquest.social.entity.FitnessGroup;
import com.fitquest.social.entity.GroupMember;
import com.fitquest.social.entity.NotificationType;
import com.fitquest.social.exception.BadRequestException;
import com.fitquest.social.exception.NotFoundException;
import com.fitquest.social.repository.FitnessGroupRepository;
import com.fitquest.social.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final FitnessGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final NotificationService notificationService;

    public List<FitnessGroupDto> myGroups(Long userId) {
        return memberRepository.findByUserId(userId).stream()
                .map(GroupMember::getGroupId)
                .distinct()
                .map(this::getGroup)
                .map(this::toDto)
                .toList();
    }

    public FitnessGroupDto create(Long coachId, String roles, CreateGroupRequest request) {
        requireCoach(roles);
        FitnessGroup group = groupRepository.save(FitnessGroup.builder()
                .name(request.name())
                .description(request.description())
                .weeklyWorkoutPlan(request.weeklyWorkoutPlan())
                .coachId(coachId)
                .createdAt(Instant.now())
                .build());
        memberRepository.save(GroupMember.builder()
                .groupId(group.getId())
                .userId(coachId)
                .displayName("Coach")
                .joinedAt(Instant.now())
                .build());
        return toDto(group);
    }

    public FitnessGroupDto addMember(Long coachId, String roles, String groupId, AddGroupMemberRequest request) {
        requireCoach(roles);
        FitnessGroup group = getGroup(groupId);
        if (!coachId.equals(group.getCoachId()) && !isAdmin(roles)) {
            throw new BadRequestException("Only the group coach can add members");
        }
        if (!memberRepository.existsByGroupIdAndUserId(groupId, request.userId())) {
            memberRepository.save(GroupMember.builder()
                    .groupId(groupId)
                    .userId(request.userId())
                    .displayName(request.displayName())
                    .joinedAt(Instant.now())
                    .build());
            members(groupId).stream()
                    .filter(member -> !member.getUserId().equals(request.userId()))
                    .forEach(member -> notificationService.createFromEvent(
                            member.getUserId(),
                            NotificationType.GROUP_JOINED,
                            "New member joined " + group.getName(),
                            memberName(request) + " joined your workout group.",
                            Map.of("groupId", groupId, "userId", request.userId()),
                            ActivityType.GROUP,
                            "New group member"
                    ));
            notificationService.createFromEvent(
                    request.userId(),
                    NotificationType.GROUP_JOINED,
                    "Added to a workout group",
                    "You joined " + group.getName() + ". Check your weekly workout plan.",
                    Map.of("groupId", groupId),
                    ActivityType.GROUP,
                    "Joined group " + group.getName()
            );
        }
        return toDto(group);
    }

    private String memberName(AddGroupMemberRequest request) {
        return request.displayName() != null && !request.displayName().isBlank()
                ? request.displayName()
                : "A new member";
    }

    public FitnessGroup getGroup(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));
    }

    public boolean isMember(String groupId, Long userId) {
        return memberRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    public List<GroupMember> members(String groupId) {
        return memberRepository.findByGroupId(groupId);
    }

    private FitnessGroupDto toDto(FitnessGroup group) {
        return new FitnessGroupDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCoachId(),
                group.getWeeklyWorkoutPlan(),
                memberRepository.findByGroupId(group.getId()).size(),
                group.getCreatedAt()
        );
    }

    private void requireCoach(String roles) {
        if (!(hasRole(roles, "ROLE_COACH") || hasRole(roles, "ROLE_ADMIN"))) {
            throw new BadRequestException("Only coaches can manage groups");
        }
    }

    private boolean isAdmin(String roles) {
        return hasRole(roles, "ROLE_ADMIN");
    }

    private boolean hasRole(String roles, String role) {
        return roles != null && List.of(roles.split(",")).contains(role);
    }
}
