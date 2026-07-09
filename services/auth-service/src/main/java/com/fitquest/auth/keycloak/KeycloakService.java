package com.fitquest.auth.keycloak;

import com.fitquest.auth.config.KeycloakProperties;
import com.fitquest.auth.dto.RegisterRequest;
import com.fitquest.auth.exception.BadRequestException;
import com.fitquest.auth.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final KeycloakProperties properties;
    private final RestClient restClient = RestClient.create();

    public boolean isEnabled() {
        return properties.enabled();
    }

    public KeycloakTokenResponse authenticate(String username, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", properties.frontendClientId());
        form.add("username", username);
        form.add("password", password);

        try {
            KeycloakTokenResponse response = restClient.post()
                    .uri(properties.tokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(KeycloakTokenResponse.class);
            if (response == null || response.accessToken() == null) {
                throw new UnauthorizedException("Invalid credentials");
            }
            return response;
        } catch (HttpClientErrorException ex) {
            throw new UnauthorizedException("Invalid credentials");
        }
    }

    public void createUser(RegisterRequest request, Long localUserId, String roleName) {
        if (!properties.enabled()) {
            return;
        }

        String adminToken = getAdminToken();
        Map<String, Object> payload = Map.of(
                "username", request.getUsername(),
                "email", request.getEmail(),
                "enabled", true,
                "emailVerified", true,
                "attributes", Map.of("fitquest_user_id", List.of(String.valueOf(localUserId))),
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", request.getPassword(),
                        "temporary", false
                ))
        );

        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri(properties.usersUri())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            assignRealmRole(adminToken, extractCreatedUserId(response), roleName);
        } catch (HttpClientErrorException.Conflict ex) {
            throw new BadRequestException("User already exists in Keycloak");
        }
    }

    public void syncFitquestUserId(String email, Long localUserId) {
        if (!properties.enabled()) {
            return;
        }

        String adminToken = getAdminToken();
        List<Map<String, Object>> users = restClient.get()
                .uri(properties.usersUri() + "?email={email}&exact=true", email)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (users == null || users.isEmpty()) {
            log.warn("Could not sync FitQuest id because no Keycloak user was found for {}", email);
            return;
        }

        Map<String, Object> userRepresentation = new HashMap<>(users.getFirst());
        Map<String, Object> attributes = new HashMap<>();
        Object existingAttributes = userRepresentation.get("attributes");
        if (existingAttributes instanceof Map<?, ?> existingMap) {
            existingMap.forEach((key, value) -> attributes.put(String.valueOf(key), value));
        }
        attributes.put("fitquest_user_id", List.of(String.valueOf(localUserId)));
        userRepresentation.put("attributes", attributes);

        restClient.put()
                .uri(properties.usersUri() + "/" + userRepresentation.get("id"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userRepresentation)
                .retrieve()
                .toBodilessEntity();
    }

    private void assignRealmRole(String adminToken, String keycloakUserId, String roleName) {
        if (keycloakUserId == null || keycloakUserId.isBlank()) {
            log.warn("Could not assign Keycloak role {} because the created user id is missing", roleName);
            return;
        }
        String roleEndpoint = properties.usersUri().replace("/users", "/roles/" + roleName);
        Map<String, Object> roleRepresentation = restClient.get()
                .uri(roleEndpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        restClient.post()
                .uri(properties.usersUri() + "/" + keycloakUserId + "/role-mappings/realm")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(roleRepresentation))
                .retrieve()
                .toBodilessEntity();
    }

    private String extractCreatedUserId(ResponseEntity<Void> response) {
        URI location = response.getHeaders().getLocation();
        if (location == null) {
            return null;
        }
        String path = location.getPath();
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }

    private String getAdminToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", properties.backendClientId());
        form.add("client_secret", properties.backendClientSecret());

        KeycloakTokenResponse response = restClient.post()
                .uri(properties.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(KeycloakTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new BadRequestException("Could not obtain Keycloak admin token");
        }
        return response.accessToken();
    }
}
