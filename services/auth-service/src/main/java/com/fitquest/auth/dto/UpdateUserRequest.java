package com.fitquest.auth.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String displayName;
    private String avatarUrl;
    private Integer age;
    private Double weightKg;
    private Double heightCm;
    private String fitnessGoal;
    private String bio;
    private String theme;
}
