package com.fitquest.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String displayName;
    private String avatarUrl;
    private Integer age;
    private Double weightKg;
    private Double heightCm;
    private String fitnessGoal;
    private String bio;

    @Builder.Default
    private String theme = "dark";
}
