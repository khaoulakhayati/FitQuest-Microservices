package com.fitquest.social.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI socialOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("FitQuest Social Service API")
                .version("1.0")
                .description("Friends, messaging, notifications, and activity feed"));
    }

    @Bean
    public OperationCustomizer userIdHeaderCustomizer() {
        return (operation, handlerMethod) -> {
            boolean needsUserId = Arrays.stream(handlerMethod.getMethodParameters())
                    .anyMatch(p -> "userId".equals(p.getParameterName())
                            && p.hasParameterAnnotation(org.springframework.web.bind.annotation.RequestHeader.class));
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
