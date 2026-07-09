package com.fitquest.social.messaging;

import com.fitquest.social.entity.ActivityType;
import com.fitquest.social.entity.NotificationType;
import com.fitquest.social.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocialEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${fitquest.rabbitmq.queue:social-service.events}")
    public void onDomainEvent(Map<String, Object> event) {
        if (event == null || event.isEmpty()) {
            return;
        }

        String eventType = stringValue(event.get("eventType"));
        String routingHint = stringValue(event.get("routingKey"));
        Long userId = extractUserId(event);

        if (userId == null) {
            log.warn("Ignoring social event without userId: {}", event);
            return;
        }

        Map<String, Object> payload = new HashMap<>(event);

        if (matches(eventType, routingHint, "WORKOUT_LOGGED", "workout.logged")) {
            handleWorkoutLogged(userId, payload);
        } else if (matches(eventType, routingHint, "ACHIEVEMENT_UNLOCKED", "achievement.unlocked")) {
            handleAchievementUnlocked(userId, payload);
        } else if (matches(eventType, routingHint, "CHALLENGE_COMPLETED", "challenge.completed")) {
            handleChallengeCompleted(userId, payload);
        } else if (matches(eventType, routingHint, "USER_REGISTERED", "user.registered")) {
            handleUserRegistered(userId, payload);
        } else if (matches(eventType, routingHint, "FRIEND_REQUEST", "friend.request")) {
            handleFriendRequest(userId, payload);
        } else {
            log.debug("Unhandled social event type={} routing={} payload={}", eventType, routingHint, event);
        }
    }

    private void handleWorkoutLogged(Long userId, Map<String, Object> payload) {
        Object calories = payload.get("caloriesBurned");
        String detail = calories != null ? "Burned " + calories + " calories" : "Great job on your workout!";
        notificationService.createFromEvent(
                userId,
                NotificationType.WORKOUT_LOGGED,
                "Workout logged",
                detail,
                payload,
                ActivityType.WORKOUT,
                "Logged a workout"
        );
    }

    private void handleAchievementUnlocked(Long userId, Map<String, Object> payload) {
        String achievement = stringValue(payload.getOrDefault("achievementName", payload.get("name")));
        String title = achievement != null ? "Achievement unlocked: " + achievement : "Achievement unlocked";
        notificationService.createFromEvent(
                userId,
                NotificationType.ACHIEVEMENT_UNLOCKED,
                title,
                "You unlocked a new achievement!",
                payload,
                ActivityType.ACHIEVEMENT,
                title
        );
    }

    private void handleChallengeCompleted(Long userId, Map<String, Object> payload) {
        String challengeTitle = stringValue(payload.get("challengeTitle"));
        String message = challengeTitle != null
                ? "You completed challenge: " + challengeTitle
                : "You completed a challenge!";
        notificationService.createFromEvent(
                userId,
                NotificationType.CHALLENGE_COMPLETED,
                "Challenge completed",
                message,
                payload,
                ActivityType.CHALLENGE,
                message
        );
    }

    private void handleUserRegistered(Long userId, Map<String, Object> payload) {
        notificationService.createFromEvent(
                userId,
                NotificationType.SYSTEM,
                "Welcome to FitQuest",
                "Your social profile is ready. Add friends and share your progress!",
                payload,
                ActivityType.FRIEND,
                "Joined FitQuest"
        );
    }

    private void handleFriendRequest(Long userId, Map<String, Object> payload) {
        Long fromUserId = extractUserId(Map.of("userId", payload.getOrDefault("fromUserId", payload.get("senderId"))));
        String message = fromUserId != null
                ? "User " + fromUserId + " sent you a friend request"
                : "You have a new friend request";
        notificationService.createFromEvent(
                userId,
                NotificationType.FRIEND_REQUEST,
                "Friend request",
                message,
                payload,
                ActivityType.FRIEND,
                message
        );
    }

    private static boolean matches(String eventType, String routingHint, String typeName, String routingKey) {
        return typeName.equalsIgnoreCase(eventType) || routingKey.equalsIgnoreCase(routingHint);
    }

    private static Long extractUserId(Map<String, Object> event) {
        Object userId = event.get("userId");
        if (userId instanceof Number number) {
            return number.longValue();
        }
        if (userId instanceof String str && !str.isBlank()) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static String stringValue(Object value) {
        return value != null ? value.toString() : null;
    }
}
