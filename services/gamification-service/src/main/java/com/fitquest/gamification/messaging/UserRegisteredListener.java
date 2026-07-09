package com.fitquest.gamification.messaging;

import com.fitquest.gamification.config.RabbitMQConfig;
import com.fitquest.gamification.service.XpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredListener {

    private final XpService xpService;

    @RabbitListener(queues = RabbitMQConfig.GAMIFICATION_QUEUE)
    public void onEvent(Map<String, Object> event) {
        Object eventType = event.get("eventType");
        if (!"USER_REGISTERED".equals(eventType)) {
            return;
        }

        Object userIdObj = event.get("userId");
        if (userIdObj == null) {
            log.warn("Ignoring user.registered event with missing userId: {}", event);
            return;
        }

        Long userId = ((Number) userIdObj).longValue();
        try {
            xpService.awardWelcomeXp(userId);
            log.info("Awarded welcome XP and newcomer badge for userId={}", userId);
        } catch (Exception ex) {
            log.warn("Could not process registration gamification for user {}: {}", userId, ex.getMessage());
        }
    }
}
