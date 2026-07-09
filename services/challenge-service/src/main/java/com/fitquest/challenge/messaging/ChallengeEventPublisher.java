package com.fitquest.challenge.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${fitquest.rabbitmq.exchange:fitquest.events}")
    private String exchange;

    @Value("${fitquest.rabbitmq.routing-key.challenge-created:challenge.created}")
    private String challengeCreatedKey;

    @Value("${fitquest.rabbitmq.routing-key.challenge-completed:challenge.completed}")
    private String challengeCompletedKey;

    @Value("${fitquest.rabbitmq.routing-key.participant-scored:participant.scored}")
    private String participantScoredKey;

    public void publishChallengeCreated(String challengeId, String title) {
        Map<String, Object> event = Map.of(
                "eventType", "CHALLENGE_CREATED",
                "challengeId", challengeId,
                "title", title
        );
        rabbitTemplate.convertAndSend(exchange, challengeCreatedKey, event);
        log.info("Published CHALLENGE_CREATED for challengeId={}", challengeId);
    }

    public void publishChallengeCompleted(String challengeId, String title) {
        Map<String, Object> event = Map.of(
                "eventType", "CHALLENGE_COMPLETED",
                "challengeId", challengeId,
                "title", title
        );
        rabbitTemplate.convertAndSend(exchange, challengeCompletedKey, event);
        log.info("Published CHALLENGE_COMPLETED for challengeId={}", challengeId);
    }

    public void publishParticipantScored(String challengeId, Long userId, int delta, int totalPoints) {
        Map<String, Object> event = Map.of(
                "eventType", "PARTICIPANT_SCORED",
                "challengeId", challengeId,
                "userId", userId,
                "pointsDelta", delta,
                "totalPoints", totalPoints
        );
        rabbitTemplate.convertAndSend(exchange, participantScoredKey, event);
        log.debug("Published PARTICIPANT_SCORED for userId={} challengeId={}", userId, challengeId);
    }
}
