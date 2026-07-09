package com.fitquest.challenge.config;

import com.fitquest.challenge.entity.*;
import com.fitquest.challenge.repository.ChallengeRepository;
import com.fitquest.challenge.repository.ParticipantRepository;
import com.fitquest.challenge.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final ChallengeRepository challengeRepository;
    private final TeamRepository teamRepository;
    private final ParticipantRepository participantRepository;

    @Bean
    CommandLineRunner seedChallengeData() {
        return args -> {
            if (challengeRepository.count() > 0) {
                return;
            }

            Instant now = Instant.now();

            Challenge summerSprint = challengeRepository.save(Challenge.builder()
                    .title("Summer Sprint 2026")
                    .description("30-day cardio and steps challenge")
                    .type(ChallengeType.TEAM)
                    .status(ChallengeStatus.ACTIVE)
                    .startDate(now.minus(5, ChronoUnit.DAYS))
                    .endDate(now.plus(25, ChronoUnit.DAYS))
                    .goalPoints(10000)
                    .build());

            Challenge soloLift = challengeRepository.save(Challenge.builder()
                    .title("Solo Lift League")
                    .description("Individual strength training leaderboard")
                    .type(ChallengeType.INDIVIDUAL)
                    .status(ChallengeStatus.ACTIVE)
                    .startDate(now)
                    .endDate(now.plus(14, ChronoUnit.DAYS))
                    .goalPoints(5000)
                    .build());

            Team thunder = teamRepository.save(Team.builder()
                    .challengeId(summerSprint.getId())
                    .name("Thunder Striders")
                    .motto("Outrun the storm")
                    .totalPoints(1250)
                    .memberIds(new ArrayList<>())
                    .build());

            Team blaze = teamRepository.save(Team.builder()
                    .challengeId(summerSprint.getId())
                    .name("Blaze Runners")
                    .motto("Fuel the fire")
                    .totalPoints(980)
                    .memberIds(new ArrayList<>())
                    .build());

            Participant p1 = participantRepository.save(Participant.builder()
                    .challengeId(summerSprint.getId())
                    .userId(1L)
                    .teamId(thunder.getId())
                    .displayName("Demo Champion")
                    .points(720)
                    .build());

            Participant p2 = participantRepository.save(Participant.builder()
                    .challengeId(summerSprint.getId())
                    .userId(2L)
                    .teamId(blaze.getId())
                    .displayName("Alex Runner")
                    .points(530)
                    .build());

            Participant p3 = participantRepository.save(Participant.builder()
                    .challengeId(soloLift.getId())
                    .userId(1L)
                    .displayName("Demo Champion")
                    .points(410)
                    .build());

            thunder.getMemberIds().addAll(List.of(p1.getId()));
            blaze.getMemberIds().addAll(List.of(p2.getId()));
            teamRepository.save(thunder);
            teamRepository.save(blaze);

            log.info("Challenge service seed data loaded ({} challenges, {} teams, {} participants)",
                    challengeRepository.count(), teamRepository.count(), participantRepository.count());
        };
    }
}
