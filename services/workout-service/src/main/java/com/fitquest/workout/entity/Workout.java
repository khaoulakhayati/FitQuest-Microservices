package com.fitquest.workout.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String difficulty;

    @Column(nullable = false)
    private Integer estimatedDurationMinutes;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean sharedTemplate = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "workout_exercises",
            joinColumns = @JoinColumn(name = "workout_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    @Builder.Default
    private Set<Exercise> exercises = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
