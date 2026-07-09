package com.fitquest.social.service;

import com.fitquest.social.dto.ActivityDto;
import com.fitquest.social.entity.FriendStatus;
import com.fitquest.social.repository.ActivityRepository;
import com.fitquest.social.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final FriendRepository friendRepository;

    public List<ActivityDto> feed(Long userId) {
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        friendRepository.findByUserIdAndStatus(userId, FriendStatus.ACCEPTED)
                .forEach(f -> userIds.add(f.getFriendUserId()));

        return activityRepository.findByUserIdInOrderByCreatedAtDesc(userIds).stream()
                .map(a -> new ActivityDto(
                        a.getId(),
                        a.getUserId(),
                        a.getActivityType(),
                        a.getSummary(),
                        a.getMetadata(),
                        a.getCreatedAt()
                ))
                .toList();
    }

    public List<ActivityDto> myActivity(Long userId) {
        return activityRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(a -> new ActivityDto(
                        a.getId(),
                        a.getUserId(),
                        a.getActivityType(),
                        a.getSummary(),
                        a.getMetadata(),
                        a.getCreatedAt()
                ))
                .toList();
    }
}
