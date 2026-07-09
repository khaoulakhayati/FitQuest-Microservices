package com.fitquest.social.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post_reactions")
@CompoundIndex(name = "idx_post_user", def = "{'postId': 1, 'userId': 1}", unique = true)
public class PostReaction {

    @Id
    private String id;

    @Indexed
    private String postId;

    @Indexed
    private Long userId;

    private VoteType vote;
}
