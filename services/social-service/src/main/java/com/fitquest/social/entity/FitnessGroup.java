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
@Document(collection = "fitness_groups")
public class FitnessGroup {

    @Id
    private String id;

    private String name;
    private String description;

    @Indexed
    private Long coachId;

    private String weeklyWorkoutPlan;
    private Instant createdAt;
}
