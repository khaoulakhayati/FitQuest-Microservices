package com.fitquest.social.config;

import com.fitquest.social.entity.Activity;
import com.fitquest.social.entity.ActivityType;
import com.fitquest.social.entity.FitnessGroup;
import com.fitquest.social.entity.Friend;
import com.fitquest.social.entity.FriendStatus;
import com.fitquest.social.entity.GroupMember;
import com.fitquest.social.entity.Message;
import com.fitquest.social.entity.Notification;
import com.fitquest.social.entity.NotificationType;
import com.fitquest.social.entity.SocialPost;
import com.fitquest.social.repository.ActivityRepository;
import com.fitquest.social.repository.FitnessGroupRepository;
import com.fitquest.social.repository.FriendRepository;
import com.fitquest.social.repository.GroupMemberRepository;
import com.fitquest.social.repository.MessageRepository;
import com.fitquest.social.repository.NotificationRepository;
import com.fitquest.social.repository.SocialPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final FriendRepository friendRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final ActivityRepository activityRepository;
    private final FitnessGroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SocialPostRepository socialPostRepository;

    @Bean
    CommandLineRunner seedSocialData() {
        return args -> {
            if (friendRepository.count() > 0) {
                return;
            }

            Instant now = Instant.now();

            friendRepository.saveAll(List.of(
                    Friend.builder().userId(1L).friendUserId(2L).friendUsername("runner42")
                            .status(FriendStatus.ACCEPTED).createdAt(now.minus(5, ChronoUnit.DAYS))
                            .updatedAt(now.minus(5, ChronoUnit.DAYS)).build(),
                    Friend.builder().userId(2L).friendUserId(1L).friendUsername("demouser")
                            .status(FriendStatus.ACCEPTED).createdAt(now.minus(5, ChronoUnit.DAYS))
                            .updatedAt(now.minus(5, ChronoUnit.DAYS)).build(),
                    Friend.builder().userId(1L).friendUserId(3L).friendUsername("stepmaster")
                            .status(FriendStatus.PENDING).createdAt(now.minus(1, ChronoUnit.DAYS))
                            .updatedAt(now.minus(1, ChronoUnit.DAYS)).build(),
                    Friend.builder().userId(3L).friendUserId(1L).friendUsername("demouser")
                            .status(FriendStatus.PENDING).createdAt(now.minus(1, ChronoUnit.DAYS))
                            .updatedAt(now.minus(1, ChronoUnit.DAYS)).build()
            ));

            messageRepository.saveAll(List.of(
                    Message.builder().senderId(1L).receiverId(2L).content("Ready for the step challenge?")
                            .read(true).sentAt(now.minus(2, ChronoUnit.HOURS)).build(),
                    Message.builder().senderId(2L).receiverId(1L).content("Always! Let's crush 10K today.")
                            .read(false).sentAt(now.minus(90, ChronoUnit.MINUTES)).build(),
                    Message.builder().senderId(2L).receiverId(1L).content("Just finished a 5K run 🏃")
                            .read(false).sentAt(now.minus(30, ChronoUnit.MINUTES)).build()
            ));

            notificationRepository.saveAll(List.of(
                    Notification.builder().userId(1L).type(NotificationType.WORKOUT_LOGGED)
                            .title("Workout logged").message("You burned 420 calories on your morning run.")
                            .payload(Map.of("workoutId", 101, "caloriesBurned", 420))
                            .read(true).createdAt(now.minus(3, ChronoUnit.HOURS)).build(),
                    Notification.builder().userId(1L).type(NotificationType.ACHIEVEMENT_UNLOCKED)
                            .title("Achievement unlocked: Early Bird")
                            .message("You logged a workout before 7 AM five days in a row.")
                            .payload(Map.of("achievementName", "Early Bird", "badgeId", "badge-early-bird"))
                            .read(false).createdAt(now.minus(1, ChronoUnit.HOURS)).build(),
                    Notification.builder().userId(2L).type(NotificationType.CHALLENGE_COMPLETED)
                            .title("Challenge completed")
                            .message("Your team finished the 10K Steps Sprint challenge!")
                            .payload(Map.of("challengeId", "ch-001", "challengeTitle", "10K Steps Sprint"))
                            .read(false).createdAt(now.minus(45, ChronoUnit.MINUTES)).build()
            ));

            activityRepository.saveAll(List.of(
                    Activity.builder().userId(1L).activityType(ActivityType.WORKOUT)
                            .summary("Logged a morning run").metadata(Map.of("caloriesBurned", 420))
                            .createdAt(now.minus(3, ChronoUnit.HOURS)).build(),
                    Activity.builder().userId(2L).activityType(ActivityType.CHALLENGE)
                            .summary("Completed 10K Steps Sprint").metadata(Map.of("challengeId", "ch-001"))
                            .createdAt(now.minus(45, ChronoUnit.MINUTES)).build(),
                    Activity.builder().userId(1L).activityType(ActivityType.ACHIEVEMENT)
                            .summary("Unlocked Early Bird badge")
                            .metadata(Map.of("achievementName", "Early Bird"))
                            .createdAt(now.minus(1, ChronoUnit.HOURS)).build()
            ));

            FitnessGroup group = groupRepository.save(FitnessGroup.builder()
                    .name("Demo Strength Crew")
                    .description("A seeded training group for the demo and coach accounts")
                    .coachId(2L)
                    .weeklyWorkoutPlan("Mon push, Wed lower body, Fri conditioning. Log each session after training.")
                    .createdAt(now.minus(6, ChronoUnit.DAYS))
                    .build());

            groupMemberRepository.saveAll(List.of(
                    GroupMember.builder().groupId(group.getId()).userId(1L).displayName("Demo Champion")
                            .joinedAt(now.minus(6, ChronoUnit.DAYS)).build(),
                    GroupMember.builder().groupId(group.getId()).userId(2L).displayName("Coach Taylor")
                            .joinedAt(now.minus(6, ChronoUnit.DAYS)).build()
            ));

            socialPostRepository.save(SocialPost.builder()
                    .groupId(group.getId())
                    .authorId(2L)
                    .authorUsername("coach")
                    .content("This week we are focusing on consistent logging and controlled progression.")
                    .upvotes(1)
                    .downvotes(0)
                    .createdAt(now.minus(4, ChronoUnit.HOURS))
                    .build());

            log.info("Social service seed data loaded (friends, messages, notifications, activities)");
        };
    }
}
