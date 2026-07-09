package com.fitquest.workout.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI workoutOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("FitQuest Workout Service API")
                .version("1.0")
                .description("Workout templates, exercise catalog, and session logging"));
    }
}
