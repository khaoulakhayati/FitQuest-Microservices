package com.fitquest.social.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "group_members")
@CompoundIndex(name = "idx_group_user", def = "{'groupId': 1, 'userId': 1}", unique = true)
public class GroupMember {

    @Id
    private String id;

    @Indexed
    private String groupId;

    @Indexed
    private Long userId;

    private String displayName;
    private Instant joinedAt;
}
