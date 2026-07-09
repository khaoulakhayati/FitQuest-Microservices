package com.fitquest.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fitquest.keycloak")
public record KeycloakProperties(
        boolean enabled,
        String tokenUri,
        String usersUri,
        String frontendClientId,
        String backendClientId,
        String backendClientSecret) {
}
