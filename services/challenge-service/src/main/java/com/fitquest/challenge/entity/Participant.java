package com.fitquest.challenge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "participants")
@CompoundIndex(name = "challenge_user_idx", def = "{'challengeId': 1, 'userId': 1}", unique = true)
public class Participant {

    @Id
    private String id;

    @Indexed
    private String challengeId;

    @Indexed
    private Long userId;

    private String teamId;

    private String displayName;

    @Builder.Default
    private int points = 0;

    @CreatedDate
    private Instant joinedAt;

    @LastModifiedDate
    private Instant updatedAt;
}
