package com.fitquest.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/register", "/auth/login", "/auth/keycloak", "/auth/refresh",
            "/actuator", "/v3/api-docs", "/swagger-ui"
    );

    @Value("${JWT_SECRET:${fitquest.jwt.secret:fitquest-super-secret-key-change-in-production-min-256-bits}}")
    private String jwtSecret;

    private final ReactiveJwtDecoder keycloakJwtDecoder;
    public JwtAuthenticationFilter(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}") String issuerUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:}") String jwkSetUri
    ) {
        super(Config.class);
        this.keycloakJwtDecoder = StringUtils.hasText(jwkSetUri)
                ? NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build()
                : null;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (isPublic(exchange)) {
                return chain.filter(exchange);
            }
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            String token = authHeader.substring(7);
            try {
                Claims claims = parseToken(token);
                String roles = firstNonBlank(claims.get("roles", String.class), "");
                if (!hasRequiredRole(config, roles)) {
                    return forbidden(exchange);
                }
                return chain.filter(withLocalClaims(exchange, claims));
            } catch (Exception e) {
                return decodeKeycloakToken(exchange, chain, token, config);
            }
        };
    }

    private boolean isPublic(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();
        if (HttpMethod.OPTIONS.equals(method)) {
            return true;
        }
        if (HttpMethod.GET.equals(method) && (path.endsWith("/api/workouts/exercises") || path.endsWith("/workouts/exercises"))) {
            return true;
        }
        return PUBLIC_PATHS.stream().anyMatch(path::contains);
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    private ServerWebExchange withLocalClaims(ServerWebExchange exchange, Claims claims) {
        return withUserHeaders(exchange, Map.of(
                "X-User-Id", claims.getSubject(),
                "X-User-Email", firstNonBlank(claims.get("email", String.class), ""),
                "X-User-Username", firstNonBlank(claims.get("username", String.class), ""),
                "X-User-Roles", firstNonBlank(claims.get("roles", String.class), ""),
                "X-Identity-Provider", firstNonBlank(claims.get("identity_provider", String.class), "")
        ));
    }

    private Mono<Void> decodeKeycloakToken(ServerWebExchange exchange,
                                           org.springframework.cloud.gateway.filter.GatewayFilterChain chain,
                                           String token,
                                           Config config) {
        if (keycloakJwtDecoder == null) {
            return unauthorized(exchange);
        }
        return keycloakJwtDecoder.decode(token)
                .onErrorResume(ex -> {
                    log.warn("Keycloak JWT rejected: {}", ex.getMessage());
                    return Mono.empty();
                })
                .flatMap(jwt -> {
                    String roles = extractRoles(jwt);
                    log.info("Accepted Keycloak JWT for path {} issuer {} user {} roles {}",
                            exchange.getRequest().getURI().getPath(),
                            jwt.getIssuer(),
                            firstNonBlank(jwt.getClaimAsString("fitquest_user_id"), jwt.getSubject()),
                            roles);
                    if (!hasRequiredRole(config, roles)) {
                        log.warn("Keycloak JWT forbidden for path {} because roles {} do not match {}",
                                exchange.getRequest().getURI().getPath(),
                                roles,
                                config.allowedRoles);
                        return forbidden(exchange);
                    }
                    return chain.filter(withKeycloakClaims(exchange, jwt, roles));
                })
                .switchIfEmpty(Mono.defer(() -> unauthorized(exchange)));
    }

    private ServerWebExchange withKeycloakClaims(ServerWebExchange exchange, Jwt jwt, String roles) {
        return withUserHeaders(exchange, Map.of(
                "X-User-Id", firstNonBlank(jwt.getClaimAsString("fitquest_user_id"), jwt.getClaimAsString("user_id"), jwt.getSubject()),
                "X-User-Email", firstNonBlank(jwt.getClaimAsString("email"), ""),
                "X-User-Username", firstNonBlank(jwt.getClaimAsString("preferred_username"), jwt.getClaimAsString("email"), ""),
                "X-User-Roles", roles,
                "X-Identity-Provider", "keycloak"
        ));
    }

    private boolean hasRequiredRole(Config config, String roles) {
        if (config == null || config.allowedRoles == null || config.allowedRoles.isEmpty()) {
            return true;
        }
        Set<String> userRoles = Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(HashSet::new));
        return config.allowedRoles.stream().anyMatch(userRoles::contains);
    }

    private ServerWebExchange withUserHeaders(ServerWebExchange exchange, Map<String, String> values) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders writableHeaders = new HttpHeaders();
        request.getHeaders().forEach((name, headerValues) ->
                writableHeaders.put(name, new ArrayList<>(headerValues)));
        values.forEach(writableHeaders::set);

        ServerHttpRequest decorated = new ServerHttpRequestDecorator(request) {
            @Override
            public HttpHeaders getHeaders() {
                return writableHeaders;
            }
        };
        return exchange.mutate().request(decorated).build();
    }

    @SuppressWarnings("unchecked")
    private String extractRoles(Jwt jwt) {
        Object realmAccess = jwt.getClaims().get("realm_access");
        if (realmAccess instanceof Map<?, ?> realmAccessMap) {
            Object roles = realmAccessMap.get("roles");
            if (roles instanceof Collection<?> roleValues) {
                return roleValues.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .collect(Collectors.joining(","));
            }
        }
        List<String> authorities = jwt.getClaimAsStringList("authorities");
        return authorities == null ? "" : String.join(",", authorities);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        private List<String> allowedRoles = List.of();

        public static Config authenticated() {
            return new Config();
        }

        public static Config roles(String... roles) {
            Config config = new Config();
            config.allowedRoles = List.of(roles);
            return config;
        }
    }
}
