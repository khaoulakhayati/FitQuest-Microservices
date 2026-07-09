package com.fitquest.challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI challengeOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("FitQuest Challenge Service API")
                .version("1.0")
                .description("Challenges, teams, participants, and leaderboards"));
    }
}
