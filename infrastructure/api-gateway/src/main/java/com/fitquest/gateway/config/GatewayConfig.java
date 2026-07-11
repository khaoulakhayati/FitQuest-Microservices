package com.fitquest.gateway.config;

import com.fitquest.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfig {

    @Value("${fitquest.routes.auth-service-uri:lb://auth-service}")
    private String authServiceUri;

    @Value("${fitquest.routes.workout-service-uri:lb://workout-service}")
    private String workoutServiceUri;

    @Value("${fitquest.routes.nutrition-service-uri:lb://nutrition-service}")
    private String nutritionServiceUri;

    @Value("${fitquest.routes.challenge-service-uri:lb://challenge-service}")
    private String challengeServiceUri;

    @Value("${fitquest.routes.gamification-service-uri:lb://gamification-service}")
    private String gamificationServiceUri;

    @Value("${fitquest.routes.social-service-uri:lb://social-service}")
    private String socialServiceUri;

    @Value("${fitquest.routes.ai-service-uri:lb://ai-service}")
    private String aiServiceUri;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, JwtAuthenticationFilter jwtFilter) {
        return builder.routes()
                .route("auth-openapi", r -> r.path("/v3/api-docs/auth")
                        .filters(f -> f.rewritePath("/v3/api-docs/auth", "/v3/api-docs"))
                        .uri(authServiceUri))
                .route("workout-openapi", r -> r.path("/v3/api-docs/workout")
                        .filters(f -> f.rewritePath("/v3/api-docs/workout", "/v3/api-docs"))
                        .uri(workoutServiceUri))
                .route("nutrition-openapi", r -> r.path("/v3/api-docs/nutrition")
                        .filters(f -> f.rewritePath("/v3/api-docs/nutrition", "/v3/api-docs"))
                        .uri(nutritionServiceUri))
                .route("challenge-openapi", r -> r.path("/v3/api-docs/challenge")
                        .filters(f -> f.rewritePath("/v3/api-docs/challenge", "/v3/api-docs"))
                        .uri(challengeServiceUri))
                .route("gamification-openapi", r -> r.path("/v3/api-docs/gamification")
                        .filters(f -> f.rewritePath("/v3/api-docs/gamification", "/v3/api-docs"))
                        .uri(gamificationServiceUri))
                .route("social-openapi", r -> r.path("/v3/api-docs/social")
                        .filters(f -> f.rewritePath("/v3/api-docs/social", "/v3/api-docs"))
                        .uri(socialServiceUri))
                .route("users-admin-read", r -> r.method(HttpMethod.GET).and().path("/api/users")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.roles("ROLE_COACH", "ROLE_ADMIN"))))
                        .uri(authServiceUri))
                .route("challenge-write", r -> r.method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE).and().path("/api/challenges", "/api/challenges/**", "/api/teams", "/api/teams/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.roles("ROLE_COACH", "ROLE_ADMIN"))))
                        .uri(challengeServiceUri))
                .route("social-groups-write", r -> r.method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE).and().path("/api/social/groups", "/api/social/groups/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.roles("ROLE_COACH", "ROLE_ADMIN"))))
                        .uri(socialServiceUri))
                .route("badges-admin-write", r -> r.method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE).and().path("/api/badges", "/api/badges/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.roles("ROLE_ADMIN"))))
                        .uri(gamificationServiceUri))
                .route("auth-service", r -> r.path("/api/auth/**", "/api/users/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.authenticated())))
                        .uri(authServiceUri))
                .route("workout-service", r -> r.path("/api/workouts", "/api/workouts/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.roles("ROLE_USER", "ROLE_COACH", "ROLE_ADMIN"))))
                        .uri(workoutServiceUri))
                .route("nutrition-service", r -> r.path("/api/nutrition/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.roles("ROLE_USER", "ROLE_COACH", "ROLE_ADMIN"))))
                        .uri(nutritionServiceUri))
                .route("challenge-service", r -> r.path("/api/challenges", "/api/challenges/**", "/api/teams", "/api/teams/**", "/api/leaderboard/challenges/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.roles("ROLE_USER", "ROLE_COACH", "ROLE_ADMIN"))))
                        .uri(challengeServiceUri))
                .route("gamification-service", r -> r.path("/api/gamification/**", "/api/xp", "/api/xp/**", "/api/badges", "/api/badges/**", "/api/leaderboard", "/api/leaderboard/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.roles("ROLE_USER", "ROLE_COACH", "ROLE_ADMIN"))))
                        .uri(gamificationServiceUri))
                .route("social-service", r -> r.path("/api/social/**", "/api/friends/**", "/api/messages/**", "/api/notifications/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.roles("ROLE_USER", "ROLE_COACH", "ROLE_ADMIN"))))
                        .uri(socialServiceUri))
                .route("ai-service", r -> r.path("/api/ai/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(JwtAuthenticationFilter.Config.roles("ROLE_USER", "ROLE_COACH", "ROLE_ADMIN"))))
                        .uri(aiServiceUri))
                .build();
    }
}
