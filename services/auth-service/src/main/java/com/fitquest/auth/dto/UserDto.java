package com.fitquest.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private Set<String> roles;
    private UserProfileDto profile;
}
