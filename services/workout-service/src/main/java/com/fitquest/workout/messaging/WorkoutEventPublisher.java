package com.fitquest.workout.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkoutEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${fitquest.rabbitmq.exchange:fitquest.events}")
    private String exchange;

    @Value("${fitquest.rabbitmq.routing-key.workout-logged:workout.logged}")
    private String workoutLoggedKey;

    public void publishWorkoutLogged(Long userId, Long workoutId, Double caloriesBurned) {
        Map<String, Object> event = Map.of(
                "eventType", "WORKOUT_LOGGED",
                "userId", userId,
                "workoutId", workoutId,
                "caloriesBurned", caloriesBurned
        );
        try {
            rabbitTemplate.convertAndSend(exchange, workoutLoggedKey, event);
            log.info("Published WORKOUT_LOGGED userId={} workoutId={} calories={}", userId, workoutId, caloriesBurned);
        } catch (Exception ex) {
            log.warn("Workout was saved, but WORKOUT_LOGGED event could not be published: {}", ex.getMessage());
        }
    }
}
