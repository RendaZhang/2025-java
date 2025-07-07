package com.renda.taskmanager.config;

import com.renda.common.dto.CommonResponseDto;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi categoriesApi(OpenApiCustomizer globalErrorCustomizer) {
        return GroupedOpenApi.builder()
                .group("categories")
                .packagesToScan("com.renda.taskmanager.controller")
                .pathsToMatch("/api/categories/**")
                .addOpenApiCustomizer(globalErrorCustomizer)
                .build();
    }

    @Bean
    public GroupedOpenApi tasksApi(OpenApiCustomizer globalErrorCustomizer) {
        return GroupedOpenApi.builder()
                .group("tasks")
                .packagesToScan("com.renda.taskmanager.controller")
                .pathsToMatch("/api/tasks/**")
                .addOpenApiCustomizer(globalErrorCustomizer)
                .build();
    }

    @Bean
    public GroupedOpenApi callsApi(OpenApiCustomizer globalErrorCustomizer) {
        return GroupedOpenApi.builder()
                .group("calls")
                .packagesToScan("com.renda.taskmanager.controller")
                .pathsToMatch("/api/calls/**")
                .addOpenApiCustomizer(globalErrorCustomizer)
                .build();
    }

    @Bean
    public OpenApiCustomizer globalErrorCustomizer() {
        return openApi -> {
            Components comps = openApi.getComponents();

            ModelConverters.getInstance().readAll(CommonResponseDto.class).forEach(comps::addSchemas);

            String schemaKey = "CommonResponseDto";

            comps.addResponses("BadRequest",
                    apiResp(schemaKey, "Bad Request", BAD_REQ_EX));

            comps.addResponses("NotFound",
                    apiResp(schemaKey, "Not Found", NOT_FOUND_EX));

            comps.addResponses("InternalError",
                    apiResp(schemaKey, "Internal Server Error", INT_ERR_EX));

            openApi.getPaths().values().forEach(p ->
                    p.readOperations().forEach(op -> {
                        op.getResponses().addApiResponse("400",
                                new ApiResponse().$ref("#/components/responses/BadRequest"));
                        op.getResponses().addApiResponse("404",
                                new ApiResponse().$ref("#/components/responses/NotFound"));
                        op.getResponses().addApiResponse("500",
                                new ApiResponse().$ref("#/components/responses/InternalError"));
                    }));
        };
    }

    private static final String BAD_REQ_EX = """
            {
              "status": 400,
              "message": "Validation failed",
              "data": [
                {"field": "title", "message": "must not be blank"},
                {"field": "status", "message": "must not be null"}
              ]
            }
            """;

    private static final String NOT_FOUND_EX = """
            {
              "status": 404,
              "message": "Task with id 99 not found",
              "data": null
            }
            """;

    private static final String INT_ERR_EX = """
            {
              "status": 500,
              "message": "Unexpected server error",
              "data": null
            }
            """;

    private ApiResponse apiResp(String schemaKey, String desc, String exampleJson) {
        return new ApiResponse().description(desc)
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(
                                        new io.swagger.v3.oas.models.media.Schema<>().$ref("#/components/schemas/" + schemaKey))
                                .example(exampleJson)));
    }

}
