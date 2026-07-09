package com.fitquest.nutrition.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI nutritionOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("FitQuest Nutrition Service API")
                .version("1.0")
                .description("Meal tracking, daily nutrition, and analytics"));
    }

    @Bean
    public OperationCustomizer userIdHeaderCustomizer() {
        return (operation, handlerMethod) -> {
            boolean needsUserId = Arrays.stream(handlerMethod.getMethodParameters())
                    .anyMatch(p -> "X-User-Id".equals(p.getParameterName()));
            if (needsUserId) {
                operation.addParametersItem(new Parameter()
                        .in("header")
                        .name("X-User-Id")
                        .required(true)
                        .description("User ID injected by API Gateway")
                        .schema(new StringSchema()));
            }
            return operation;
        };
    }
}
