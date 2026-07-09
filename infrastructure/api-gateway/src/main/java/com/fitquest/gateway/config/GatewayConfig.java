package com.fitquest.gateway.config;

import com.fitquest.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .route("auth-service", r -> r.path("/api/auth/**", "/api/users/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(authServiceUri))
                .route("workout-service", r -> r.path("/api/workouts", "/api/workouts/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(workoutServiceUri))
                .route("nutrition-service", r -> r.path("/api/nutrition/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(nutritionServiceUri))
                .route("challenge-service", r -> r.path("/api/challenges", "/api/challenges/**", "/api/teams", "/api/teams/**", "/api/leaderboard/challenges/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(challengeServiceUri))
                .route("gamification-service", r -> r.path("/api/gamification/**", "/api/xp", "/api/xp/**", "/api/badges", "/api/badges/**", "/api/leaderboard", "/api/leaderboard/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(gamificationServiceUri))
                .route("social-service", r -> r.path("/api/social/**", "/api/friends/**", "/api/messages/**", "/api/notifications/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(socialServiceUri))
                .route("ai-service", r -> r.path("/api/ai/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(aiServiceUri))
                .build();
    }
}
