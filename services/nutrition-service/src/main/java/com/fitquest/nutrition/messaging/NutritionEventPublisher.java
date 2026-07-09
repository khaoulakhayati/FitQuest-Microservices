package com.fitquest.nutrition.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NutritionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${fitquest.rabbitmq.exchange:fitquest.events}")
    private String exchange;

    @Value("${fitquest.rabbitmq.routing-key.meal-logged:nutrition.meal.logged}")
    private String mealLoggedKey;

    public void publishMealLogged(Long userId, Long mealId, int calories) {
        Map<String, Object> event = Map.of(
                "eventType", "MEAL_LOGGED",
                "userId", userId,
                "mealId", mealId,
                "calories", calories
        );
        try {
            rabbitTemplate.convertAndSend(exchange, mealLoggedKey, event);
            log.info("Published MEAL_LOGGED for userId={}, mealId={}", userId, mealId);
        } catch (Exception ex) {
            log.warn("Meal was saved, but MEAL_LOGGED event could not be published: {}", ex.getMessage());
        }
    }
}
