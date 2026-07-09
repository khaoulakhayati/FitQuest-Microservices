package com.fitquest.challenge.messaging;

import com.fitquest.challenge.dto.AddPointsRequest;
import com.fitquest.challenge.entity.Participant;
import com.fitquest.challenge.repository.ParticipantRepository;
import com.fitquest.challenge.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkoutCompletedListener {

    private final ParticipantRepository participantRepository;
    private final ParticipantService participantService;

    @RabbitListener(queues = "${fitquest.rabbitmq.queue.workout-completed:challenge.workout.completed}")
    public void onWorkoutCompleted(Map<String, Object> event) {
        Object userIdObj = event.get("userId");
        Object pointsObj = event.get("points");
        if (userIdObj == null || pointsObj == null) {
            log.warn("Ignoring workout.completed event with missing userId or points: {}", event);
            return;
        }
        Long userId = ((Number) userIdObj).longValue();
        int points = ((Number) pointsObj).intValue();

        for (Participant participant : participantRepository.findAll()) {
            if (!userId.equals(participant.getUserId())) {
                continue;
            }
            try {
                participantService.addPoints(participant.getChallengeId(), new AddPointsRequest(userId, points));
            } catch (Exception ex) {
                log.warn("Could not award points for challenge {} user {}: {}",
                        participant.getChallengeId(), userId, ex.getMessage());
            }
        }
    }
}
