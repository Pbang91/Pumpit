package com.example.pumpit.global.config;

import com.example.pumpit.global.exception.annotation.ApiExceptionData;
import com.example.pumpit.global.exception.annotation.ApiExceptionResponse;
import com.example.pumpit.global.exception.enums.CustomExceptionData;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@OpenAPIDefinition(
        info = @Info(
                title = "PumpIt API",
                description = "PumpIt API Documentation"
        )
)
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components().addSecuritySchemes(
                                securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            ApiExceptionResponse apiExceptionResponse = handlerMethod.getMethodAnnotation(ApiExceptionResponse.class);

            if (apiExceptionResponse != null) {
                ApiResponses apiResponses = operation.getResponses();
                ApiExceptionData[] exceptionDataList = apiExceptionResponse.value();

                HashMap<HttpStatus, Map<String, Example>> statusToExamples = new HashMap<>();

                for (ApiExceptionData exceptionData : exceptionDataList) {
                    CustomExceptionData errorCode = exceptionData.errorCode();
                    String details = exceptionData.details();

                    Map<String, Object> exampleBody = Map.of(
                            "code", errorCode.getCode(),
                            "description", errorCode.getDescription(),
                            "details", details
                    );

                    Example example = new Example()
                            .summary(errorCode.getCode()) // 예시 코드 요약 표시
                            .value(exampleBody); // 실제 JSON body

                    // 상태 코드별로 묶기
                    statusToExamples
                            .computeIfAbsent(errorCode.getStatus(), k -> new HashMap<>())
                            .put(errorCode.getCode(), example);
                }

                statusToExamples.forEach((status, exampleMap) -> {
                    Content content = new Content().addMediaType(
                            "application/json",
                            new MediaType().examples(exampleMap)
                    );

                    ApiResponse apiResponse = new ApiResponse()
                            .description(status.getReasonPhrase())
                            .content(content);

                    apiResponses.addApiResponse(String.valueOf(status.value()), apiResponse);
                });
            }

            return operation;
        };
    }

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .addOperationCustomizer(operationCustomizer())
                .build();
    }
}