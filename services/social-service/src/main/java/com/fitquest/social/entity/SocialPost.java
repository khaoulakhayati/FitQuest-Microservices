package com.fitquest.social.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "social_posts")
public class SocialPost {

    @Id
    private String id;

    @Indexed
    private String groupId;

    @Indexed
    private Long authorId;

    private String authorUsername;
    private String content;
    private int upvotes;
    private int downvotes;
    private Instant createdAt;
}
