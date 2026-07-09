package com.fitquest.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDto {
    private String displayName;
    private String avatarUrl;
    private Integer age;
    private Double weightKg;
    private Double heightCm;
    private String fitnessGoal;
    private String bio;
    private String theme;
}
