package com.fitquest.social.service;

import com.fitquest.social.dto.CreatePostRequest;
import com.fitquest.social.dto.SocialPostDto;
import com.fitquest.social.entity.*;
import com.fitquest.social.exception.BadRequestException;
import com.fitquest.social.exception.NotFoundException;
import com.fitquest.social.repository.GroupMemberRepository;
import com.fitquest.social.repository.PostReactionRepository;
import com.fitquest.social.repository.SocialPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SocialPostService {

    private final SocialPostRepository postRepository;
    private final PostReactionRepository reactionRepository;
    private final GroupMemberRepository memberRepository;
    private final GroupService groupService;
    private final NotificationService notificationService;

    public List<SocialPostDto> feed(Long userId) {
        List<String> groupIds = memberRepository.findByUserId(userId).stream()
                .map(GroupMember::getGroupId)
                .distinct()
                .toList();
        if (groupIds.isEmpty()) {
            return List.of();
        }
        return postRepository.findByGroupIdInOrderByCreatedAtDesc(groupIds).stream()
                .map(post -> toDto(post, userId))
                .toList();
    }

    public SocialPostDto create(Long userId, String username, String groupId, CreatePostRequest request) {
        FitnessGroup group = groupService.getGroup(groupId);
        requireMember(groupId, userId);
        SocialPost post = postRepository.save(SocialPost.builder()
                .groupId(groupId)
                .authorId(userId)
                .authorUsername(username != null ? username : "User " + userId)
                .content(request.content())
                .upvotes(0)
                .downvotes(0)
                .createdAt(Instant.now())
                .build());

        groupService.members(groupId).stream()
                .filter(member -> !member.getUserId().equals(userId))
                .forEach(member -> notificationService.createFromEvent(
                        member.getUserId(),
                        NotificationType.POST_CREATED,
                        "New post in " + group.getName(),
                        post.getAuthorUsername() + " shared a new post.",
                        Map.of("groupId", groupId, "postId", post.getId()),
                        ActivityType.SOCIAL,
                        "New group post"
                ));

        return toDto(post, userId);
    }

    public SocialPostDto react(Long userId, String postId, VoteType vote) {
        SocialPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found: " + postId));
        requireMember(post.getGroupId(), userId);

        reactionRepository.findByPostIdAndUserId(postId, userId).ifPresentOrElse(existing -> {
            if (existing.getVote() == vote) {
                applyDelta(post, existing.getVote(), -1);
                reactionRepository.delete(existing);
            } else {
                applyDelta(post, existing.getVote(), -1);
                existing.setVote(vote);
                applyDelta(post, vote, 1);
                reactionRepository.save(existing);
            }
        }, () -> {
            reactionRepository.save(PostReaction.builder()
                    .postId(postId)
                    .userId(userId)
                    .vote(vote)
                    .build());
            applyDelta(post, vote, 1);
        });

        SocialPost savedPost = postRepository.save(post);
        if (!savedPost.getAuthorId().equals(userId)) {
            notificationService.createFromEvent(
                    savedPost.getAuthorId(),
                    NotificationType.POST_REACTED,
                    "Someone reacted to your post",
                    "Your group post received a " + vote.name().toLowerCase() + "vote.",
                    Map.of("groupId", savedPost.getGroupId(), "postId", savedPost.getId(), "vote", vote.name()),
                    ActivityType.SOCIAL,
                    "Post reaction"
            );
        }
        return toDto(savedPost, userId);
    }

    private void applyDelta(SocialPost post, VoteType vote, int delta) {
        if (vote == VoteType.UP) {
            post.setUpvotes(Math.max(0, post.getUpvotes() + delta));
        } else {
            post.setDownvotes(Math.max(0, post.getDownvotes() + delta));
        }
    }

    private void requireMember(String groupId, Long userId) {
        if (!groupService.isMember(groupId, userId)) {
            throw new BadRequestException("You can only use posts from your group");
        }
    }

    private SocialPostDto toDto(SocialPost post, Long viewerId) {
        String myVote = reactionRepository.findByPostIdAndUserId(post.getId(), viewerId)
                .map(reaction -> reaction.getVote().name())
                .orElse(null);
        return new SocialPostDto(
                post.getId(),
                post.getGroupId(),
                post.getAuthorId(),
                post.getAuthorUsername(),
                post.getContent(),
                post.getUpvotes(),
                post.getDownvotes(),
                myVote,
                post.getCreatedAt()
        );
    }
}
