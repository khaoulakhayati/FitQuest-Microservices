package com.fitquest.social.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "friends")
@CompoundIndex(name = "user_friend_idx", def = "{'userId': 1, 'friendUserId': 1}", unique = true)
public class Friend {

    @Id
    private String id;

    private Long userId;
    private Long friendUserId;
    private String friendUsername;
    private FriendStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
