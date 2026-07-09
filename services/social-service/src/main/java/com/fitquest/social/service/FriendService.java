package com.fitquest.social.service;

import com.fitquest.social.dto.FriendDto;
import com.fitquest.social.dto.SendFriendRequest;
import com.fitquest.social.entity.Friend;
import com.fitquest.social.entity.FriendStatus;
import com.fitquest.social.entity.NotificationType;
import com.fitquest.social.entity.ActivityType;
import com.fitquest.social.exception.BadRequestException;
import com.fitquest.social.exception.NotFoundException;
import com.fitquest.social.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final NotificationService notificationService;

    public List<FriendDto> listFriends(Long userId) {
        return friendRepository.findByUserIdAndStatus(userId, FriendStatus.ACCEPTED).stream()
                .map(this::toDto)
                .toList();
    }

    public List<FriendDto> listPending(Long userId) {
        return friendRepository.findByUserIdAndStatus(userId, FriendStatus.PENDING).stream()
                .map(this::toDto)
                .toList();
    }

    public FriendDto sendRequest(Long userId, SendFriendRequest request) {
        if (userId.equals(request.friendUserId())) {
            throw new BadRequestException("Cannot add yourself as a friend");
        }
        if (friendRepository.existsByUserIdAndFriendUserId(userId, request.friendUserId())) {
            throw new BadRequestException("Friend relationship already exists");
        }

        Instant now = Instant.now();
        Friend outgoing = friendRepository.save(Friend.builder()
                .userId(userId)
                .friendUserId(request.friendUserId())
                .friendUsername(request.friendUsername() != null ? request.friendUsername() : "user-" + request.friendUserId())
                .status(FriendStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build());

        Friend incoming = friendRepository.save(Friend.builder()
                .userId(request.friendUserId())
                .friendUserId(userId)
                .friendUsername("user-" + userId)
                .status(FriendStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build());

        notificationService.createFromEvent(
                request.friendUserId(),
                NotificationType.FRIEND_REQUEST,
                "New friend request",
                "User " + userId + " sent you a friend request",
                Map.of("fromUserId", userId, "friendId", outgoing.getId()),
                ActivityType.FRIEND,
                "Friend request from user " + userId
        );

        return toDto(outgoing);
    }

    public FriendDto accept(Long userId, String friendId) {
        Friend pending = friendRepository.findById(friendId)
                .filter(f -> userId.equals(f.getUserId()) && f.getStatus() == FriendStatus.PENDING)
                .orElseThrow(() -> new NotFoundException("Pending friend request not found: " + friendId));

        Instant now = Instant.now();
        pending.setStatus(FriendStatus.ACCEPTED);
        pending.setUpdatedAt(now);
        friendRepository.save(pending);

        Friend reciprocal = friendRepository
                .findByUserIdAndFriendUserId(pending.getFriendUserId(), userId)
                .orElseThrow(() -> new NotFoundException("Reciprocal friend record missing"));
        reciprocal.setStatus(FriendStatus.ACCEPTED);
        reciprocal.setUpdatedAt(now);
        friendRepository.save(reciprocal);

        notificationService.createFromEvent(
                pending.getFriendUserId(),
                NotificationType.FRIEND_ACCEPTED,
                "Friend request accepted",
                "User " + userId + " accepted your friend request",
                Map.of("friendUserId", userId),
                ActivityType.FRIEND,
                "Friend request accepted by user " + userId
        );

        return toDto(pending);
    }

    public void remove(Long userId, String friendId) {
        Friend friend = friendRepository.findById(friendId)
                .filter(f -> userId.equals(f.getUserId()))
                .orElseThrow(() -> new NotFoundException("Friend not found: " + friendId));

        friendRepository.findByUserIdAndFriendUserId(friend.getFriendUserId(), userId)
                .ifPresent(friendRepository::delete);
        friendRepository.delete(friend);
    }

    private FriendDto toDto(Friend friend) {
        return new FriendDto(
                friend.getId(),
                friend.getUserId(),
                friend.getFriendUserId(),
                friend.getFriendUsername(),
                friend.getStatus(),
                friend.getCreatedAt()
        );
    }
}
