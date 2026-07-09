package com.fitquest.auth.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${fitquest.rabbitmq.exchange:fitquest.events}")
    private String exchange;

    @Value("${fitquest.rabbitmq.routing-key.user-registered:user.registered}")
    private String userRegisteredKey;

    public void publishUserRegistered(Long userId, String email, String username) {
        Map<String, Object> event = Map.of(
                "eventType", "USER_REGISTERED",
                "userId", userId,
                "email", email,
                "username", username
        );
        rabbitTemplate.convertAndSend(exchange, userRegisteredKey, event);
        log.info("Published USER_REGISTERED for userId={}", userId);
    }
}
