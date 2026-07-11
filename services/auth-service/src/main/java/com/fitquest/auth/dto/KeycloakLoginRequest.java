package com.fitquest.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KeycloakLoginRequest {
    @NotBlank
    private String accessToken;
}
