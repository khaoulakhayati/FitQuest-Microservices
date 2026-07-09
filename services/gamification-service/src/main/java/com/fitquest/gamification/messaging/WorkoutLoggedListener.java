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
public class WorkoutLoggedListener {

    private final XpService xpService;

    @RabbitListener(queues = RabbitMQConfig.GAMIFICATION_QUEUE)
    public void onEvent(Map<String, Object> event) {
        Object eventType = event.get("eventType");
        if (!"WORKOUT_LOGGED".equals(eventType)) {
            return;
        }

        Object userIdObj = event.get("userId");
        if (userIdObj == null) {
            log.warn("Ignoring workout.logged event with missing userId: {}", event);
            return;
        }

        Long userId = ((Number) userIdObj).longValue();
        Long workoutId = event.get("workoutId") != null
                ? ((Number) event.get("workoutId")).longValue()
                : null;
        double calories = event.get("caloriesBurned") != null
                ? ((Number) event.get("caloriesBurned")).doubleValue()
                : 0.0;

        try {
            xpService.awardWorkoutXp(userId, workoutId, calories);
            log.info("Awarded workout XP for userId={} workoutId={}", userId, workoutId);
        } catch (Exception ex) {
            log.warn("Could not award workout XP for user {}: {}", userId, ex.getMessage());
        }
    }
}
