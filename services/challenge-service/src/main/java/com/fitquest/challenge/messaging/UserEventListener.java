package com.fitquest.challenge.messaging;

import com.fitquest.challenge.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class UserEventListener {

    @RabbitListener(queues = RabbitMQConfig.USER_EVENTS_QUEUE)
    public void onUserEvent(Map<String, Object> event) {
        Object eventType = event.get("eventType");
        if ("USER_REGISTERED".equals(eventType)) {
            log.info("Received USER_REGISTERED for userId={}", event.get("userId"));
        }
    }
}
