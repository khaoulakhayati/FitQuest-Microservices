package com.fitquest.social.service;

import com.fitquest.social.dto.NotificationDto;
import com.fitquest.social.dto.NotificationPollResponse;
import com.fitquest.social.entity.Activity;
import com.fitquest.social.entity.ActivityType;
import com.fitquest.social.entity.Notification;
import com.fitquest.social.entity.NotificationType;
import com.fitquest.social.exception.NotFoundException;
import com.fitquest.social.repository.ActivityRepository;
import com.fitquest.social.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ActivityRepository activityRepository;
    private final NotificationSseService sseService;

    public List<NotificationDto> findAll(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<NotificationDto> findUnread(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    public NotificationPollResponse poll(Long userId, Instant since) {
        Instant effectiveSince = since != null ? since : Instant.EPOCH;
        List<NotificationDto> items = notificationRepository
                .findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(userId, effectiveSince)
                .stream()
                .map(this::toDto)
                .toList();
        long unread = notificationRepository.countByUserIdAndReadFalse(userId);
        return new NotificationPollResponse(items, unread, Instant.now());
    }

    public NotificationDto markRead(Long userId, String id) {
        Notification notification = notificationRepository.findById(id)
                .filter(n -> userId.equals(n.getUserId()))
                .orElseThrow(() -> new NotFoundException("Notification not found: " + id));
        notification.setRead(true);
        return toDto(notificationRepository.save(notification));
    }

    public void markAllRead(Long userId) {
        notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .forEach(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
    }

    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public NotificationDto createFromEvent(
            Long userId,
            NotificationType type,
            String title,
            String message,
            Map<String, Object> payload,
            ActivityType activityType,
            String activitySummary
    ) {
        Instant now = Instant.now();
        Notification notification = notificationRepository.save(Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .payload(payload)
                .read(false)
                .createdAt(now)
                .build());

        activityRepository.save(Activity.builder()
                .userId(userId)
                .activityType(activityType)
                .summary(activitySummary)
                .metadata(payload)
                .createdAt(now)
                .build());

        NotificationDto dto = toDto(notification);
        sseService.broadcast(userId, dto);
        return dto;
    }

    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getPayload(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
