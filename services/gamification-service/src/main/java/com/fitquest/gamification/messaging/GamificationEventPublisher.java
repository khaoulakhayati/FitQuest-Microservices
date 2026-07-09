package com.fitquest.gamification.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GamificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${fitquest.rabbitmq.exchange:fitquest.events}")
    private String exchange;

    @Value("${fitquest.rabbitmq.routing-key.achievement-unlocked:achievement.unlocked}")
    private String achievementUnlockedKey;

    public void publishAchievementUnlocked(Long userId, String badgeCode, String reason) {
        Map<String, Object> event = Map.of(
                "eventType", "ACHIEVEMENT_UNLOCKED",
                "userId", userId,
                "badgeCode", badgeCode,
                "reason", reason
        );
        rabbitTemplate.convertAndSend(exchange, achievementUnlockedKey, event);
        log.info("Published achievement.unlocked userId={} badge={}", userId, badgeCode);
    }
}
