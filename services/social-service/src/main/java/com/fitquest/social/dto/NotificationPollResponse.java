package com.fitquest.social.dto;

import java.time.Instant;
import java.util.List;

public record NotificationPollResponse(
        List<NotificationDto> notifications,
        long unreadCount,
        Instant polledAt
) {
}
